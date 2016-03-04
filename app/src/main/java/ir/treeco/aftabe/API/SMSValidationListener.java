package ir.treeco.aftabe.API;

import ir.treeco.aftabe.API.Utils.SMSToken;

/**
 * Created by al on 3/4/16.
 */
public interface SMSValidationListener {

    void onSMSValidateSent(SMSToken smsToken);

    void onSMSValidationFail();
}
