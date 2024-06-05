package kopo.poly.service.impl;

import kopo.poly.dto.LikeJobDTO;
import kopo.poly.dto.TestResultDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IAdminMapper;
import kopo.poly.persistance.mapper.IProfileMapper;
import kopo.poly.service.IAdminService;
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
public class AdminService implements IAdminService {

    private final IAdminMapper adminMapper;

    @Override
    public List<UserInfoDTO> getUserList() throws Exception {

        log.info(this.getClass().getName() + ".getUserList Start!");

        List<UserInfoDTO> rList = Optional.ofNullable(adminMapper.getUserList()).orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".getUserList End!");

        return rList;
    }
    
    @Override
    public UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserInfo Start!");

        UserInfoDTO rDTO = Optional.ofNullable(adminMapper.getUserInfo(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getUserInfo End!");

        return rDTO;
    }

    @Override
    public void updateUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateUserInfo Start!");

        adminMapper.updateUserInfo(pDTO);

        log.info(this.getClass().getName() + ".updateUserInfo End!");

    }

    @Override
    public void deleteUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        adminMapper.deleteUserInfo(pDTO);

        log.info(this.getClass().getName() + ".deleteUserInfo End!");

    }

}
