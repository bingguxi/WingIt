// let remoteStreamElement = document.querySelector('#remoteStream');
let localStreamElement = document.querySelector('#localStream');
const myKey = Math.random().toString(36).substring(2, 11);
let pcListMap = new Map();
// let roomName;
let otherKeyList = [];
let localStream = undefined;

const startCam = async () =>{

    // 비디오 요소를 찾을 때까지 기다리도록 함수를 변경합니다.
    const localStreamElement = document.querySelector('#localStream');

    if (localStreamElement) {

        if (navigator.mediaDevices !== undefined) {
            await navigator.mediaDevices.getUserMedia({audio: true, video: true})
                .then(async (stream) => {
                    console.log('Stream found');

                    console.log("stream : ", stream);

                    localStream = stream;
                    console.log("localStream : ", localStream)

                    // Enable the cam by default
                    stream.getVideoTracks()[0].enabled = true;

                    // Disable the microphone by default
                    stream.getAudioTracks()[0].enabled = false;

                    // 비디오 요소를 표시하기 전에 display 속성을 변경
                    document.querySelector('#localStream').style.display = 'block';

                    localStreamElement.srcObject = localStream;
                    console.log("localStreamElement.srcObject : ", localStreamElement.srcObject)

                    // Connect after making sure that local stream is availble

                }).catch(error => {
                    console.error("Error accessing media devices:", error);
                });

        } else {

            console.error("Local video element not found");

        }

    }
}

const connectSocket = async () =>{
    const socket = new SockJS('/signaling');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({
        'roomName': roomName,
        'camKey': myKey
    }, function () {
        console.log('Connected to WebRTC server');

        // iceCandidate 를 구독 해준다.
        stompClient.subscribe(`/topic/peer/iceCandidate/${myKey}/${roomName}`, candidate => {
            const key = JSON.parse(candidate.body).key
            const message = JSON.parse(candidate.body).body;

            //해당 신호를 Peer에 추가해준다.
            pcListMap.get(key).addIceCandidate(new RTCIceCandidate({ candidate: message.candidate, sdpMLineIndex: message.sdpMLineIndex, sdpMid: message.sdpMid }));

        });

        //offer 를 구독 해준다.
        stompClient.subscribe(`/topic/peer/offer/${myKey}/${roomName}`, offer => {

            const key = JSON.parse(offer.body).key;
            const message = JSON.parse(offer.body).body;

            //해당 키에 대한 새로운 peer를 생성하여 map 에 저장한다.
            pcListMap.set(key, createPeerConnection(key));
            //새로 만든 peer에 RTCSessionDescription를 추가해준다.
            pcListMap.get(key).setRemoteDescription(new RTCSessionDescription({ type: message.type, sdp: message.sdp }));
            //받은 키에 대한 answer를 보낸다.
            sendAnswer(pcListMap.get(key), key);

        });

        //answer 를 구독 해준다.
        stompClient.subscribe(`/topic/peer/answer/${myKey}/${roomName}`, answer => {
            const key = JSON.parse(answer.body).key;
            const message = JSON.parse(answer.body).body;

            //받은 키에 대한 peer에 description 해준다.
            pcListMap.get(key).setRemoteDescription(new RTCSessionDescription(message));

        });

        stompClient.subscribe(`/topic/call/key`, message =>{
            stompClient.send(`/app/send/key`, {}, JSON.stringify(myKey));

        });

        stompClient.subscribe(`/topic/send/key`, message => {
            const key = JSON.parse(message.body);

            if(myKey !== key && otherKeyList.find((mapKey) => mapKey === myKey) === undefined){
                otherKeyList.push(key);
            }
        });

        // 연결 종료 알림을 처리하는 구독 추가
        stompClient.subscribe(`/topic/disconnect/${roomName}`, message => {
            const key = JSON.parse(message.body).key;
            console.log(`Peer disconnected: ${key}`);

            // 원격 비디오 요소 제거
            const remoteVideoElement = document.getElementById(key);
            if (remoteVideoElement) {
                remoteVideoElement.srcObject = null;
                remoteVideoElement.remove();
            }
        });

        // 웹소켓 연결이 끊어졌을 때 처리
        socket.onclose = () => {
            console.log('Disconnected from WebRTC server');
            // 로컬 스트림 비디오 요소 제거
            if (localStreamElement) {
                localStreamElement.srcObject = null;
                localStreamElement.style.display = 'none';
            }

            // 다른 사용자들에게 연결 종료 알림
            stompClient.send(`/app/disconnect/${roomName}`, {}, JSON.stringify({ key: myKey }));
        };

        // 웹소켓 연결 완료 후 startStream 함수 호출
        startStream();

    });
}

let onTrack = (event, otherKey) => {

    if(document.getElementById(`${otherKey}`) === null){
        const video =  document.createElement('video');

        video.autoplay = true;
        video.controls = true;
        video.id = otherKey;
        video.srcObject = event.streams[0];

        document.getElementById('remoteStreamDiv').appendChild(video);
    }

    //
    // remoteStreamElement.srcObject = event.streams[0];
    // remoteStreamElement.play();
};

const createPeerConnection = (otherKey) =>{
    const pc = new RTCPeerConnection();
    try {
        pc.addEventListener('icecandidate', (event) =>{
            onIceCandidate(event, otherKey);
        });
        pc.addEventListener('track', (event) =>{
            onTrack(event, otherKey);
        });
        if(localStream !== undefined){
            localStream.getTracks().forEach(track => {
                pc.addTrack(track, localStream);
            });
        }

        console.log('PeerConnection created');
    } catch (error) {
        console.error('PeerConnection failed: ', error);
    }
    return pc;
}

let onIceCandidate = (event, otherKey) => {
    if (event.candidate) {
        console.log('ICE candidate');
        stompClient.send(`/app/peer/iceCandidate/${otherKey}/${roomName}`,{}, JSON.stringify({
            key : myKey,
            body : event.candidate
        }));
    }
};

let sendOffer = (pc ,otherKey) => {
    pc.createOffer().then(offer =>{
        setLocalAndSendMessage(pc, offer);
        stompClient.send(`/app/peer/offer/${otherKey}/${roomName}`, {}, JSON.stringify({
            key : myKey,
            body : offer
        }));
        console.log('Send offer');
    });
};

let sendAnswer = (pc,otherKey) => {
    pc.createAnswer().then( answer => {
        setLocalAndSendMessage(pc ,answer);
        stompClient.send(`/app/peer/answer/${otherKey}/${roomName}`, {}, JSON.stringify({
            key : myKey,
            body : answer
        }));
        console.log('Send answer');
    });
};

const setLocalAndSendMessage = (pc ,sessionDescription) =>{
    pc.setLocalDescription(sessionDescription);
}

//룸 번호 입력 후 캠 + 웹소켓 실행
// document.querySelector('#enterRoomBtn').addEventListener('click', async () =>{

$(document).ready(async function () {

    // 캠 시작
    // await startCam();
    console.log("startCam 실행하겠습니다~~~~");
    await startCam();
    console.log("startCam 끝났습니다~~~~");

    // 웹소켓 연결
    // await connectSocket();
    console.log("connectSocket 실행하겠습니다~~~~");
    await connectSocket();
    console.log("connectSocket 끝났습니다~~~~");

    // 피어들끼리 연결
    // console.log("startStream 실행합니다요");
    // startStream();
    // console.log("startStream 끝!!!");

})


// 스트림 버튼 클릭시 , 다른 웹 key들 웹소켓을 가져 온뒤에 offer -> answer -> iceCandidate 통신
// peer 커넥션은 pcListMap 으로 저장
const startStream = async () => {

    console.log("startStream 실행 시작!")

    await stompClient.send(`/app/call/key`, {}, {});

    setTimeout(() =>{

        otherKeyList.map((key) =>{
            if(!pcListMap.has(key)){
                pcListMap.set(key, createPeerConnection(key));
                sendOffer(pcListMap.get(key),key);
            }

        });

    },1000);

    console.log("startStream 실행 끝!");

};
