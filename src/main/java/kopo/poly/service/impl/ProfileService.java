package kopo.poly.service.impl;

import kopo.poly.dto.LikeJobDTO;
import kopo.poly.dto.TestResultDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IProfileMapper;
import kopo.poly.service.IProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService implements IProfileService {

    private final IProfileMapper profileMapper;

    @Override
    public UserInfoDTO getProfile(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getProfile Start!");

        UserInfoDTO rDTO = Optional.ofNullable(profileMapper.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getProfile End!");

        return rDTO;
    }

    @Override
    public void updateProfile(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateProfile Start!");

        profileMapper.updateProfile(pDTO);

        log.info(this.getClass().getName() + ".updateProfile End!");

    }

    @Override
    public void deleteUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        profileMapper.deleteUserInfo(pDTO);

        log.info(this.getClass().getName() + ".deleteUserInfo End!");

    }

    @Override
    public List<TestResultDTO> getTestRecordList(TestResultDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getTestResult Start!");

        log.info("컨트롤러에서 넘어온 pDTO : " + pDTO.toString());

        List<TestResultDTO> rList = Optional.ofNullable(profileMapper.getTestRecordList(pDTO))
                .orElseGet(ArrayList::new);

        // rList의 각 TestResultDTO 객체의 testType 값을 검사 및 수정
        List<TestResultDTO> modifiedList = rList.stream().map(dto -> {

            String updatedTestType = dto.getTestType();

            if ("HMstudent".equals(dto.getTestType())) {

                updatedTestType = "직업흥미검사(K)";

            } else if ("GCGstudent".equals(dto.getTestType()) || "GCGadult".equals(dto.getTestType())) {

                updatedTestType = "직업가치관검사";

            }

            return dto.toBuilder().testType(updatedTestType).build();
        }).collect(Collectors.toList());

        log.info(this.getClass().getName() + ".getTestResult End!");

        return modifiedList;

    }

    @Override
    public void deleteTestRecord(TestResultDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteTestRecord Start!");

        profileMapper.deleteTestRecord(pDTO);

        log.info(this.getClass().getName() + ".deleteTestRecord End!");

    }

    @Override
    public List<LikeJobDTO> getLikeJobList(LikeJobDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getLikeJobList Start!");

        log.info("컨트롤러에서 넘어온 pDTO : " + pDTO.toString());

        List<LikeJobDTO> rList = Optional.ofNullable(profileMapper.getLikeJobList(pDTO))
                        .orElseGet(ArrayList::new);

        log.info("rList : " + rList);

        log.info(this.getClass().getName() + ".getLikeJobList End!");

        return rList;

    }

}
