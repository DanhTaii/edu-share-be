package vn.edu.nlu.edushare.edu_share.api.user_fcm_token.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.nlu.edushare.edu_share.api.user_fcm_token.service.FcmService;

@RestController
@RequestMapping("/fcm/tokens")
public class UserFcmTokenController {

    @Autowired
    private FcmService userFcmTokenService;

//    @PostMapping
//    public SaveFcmTokenRequest saveFcmToken(String userId, String fcmToken) {
////        return userFcmTokenService.sendNotificationToUser(userId, "Title", "Body");
//    }


}
