package edu.thejoeun.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface EmailMapper {

    int updateAuthKey(Map<String, String> map) ;

    int insertAuthKey(Map<String, String> map) ;

    String getAuthKeyByEmail(Map<String, Object> map) ;

    int deleteAuthKey(Map<String, Object> map);

    int checkAuthKey(Map<String, Object> map) ;
}
