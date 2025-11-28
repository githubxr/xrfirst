package org.first.user.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    

    //可通过 @JSONField(name = "newName") 注解修改字段在 JSONObject 中的键名：
    private String userId;
    private String userCode;
    private String userName;
    private String pwdMD5;
    private String telPhone;

}
