package edu.thejoeun.member.model.mapper;

import edu.thejoeun.member.model.dto.Email;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmailMapper {

    void insertAuthKey(Map<String, String> map);

    Email getAuthKeyByEmail(String email);

    void deleteAuthKey(String email);
}