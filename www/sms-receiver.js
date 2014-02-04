var smsreceiver = {
    
    listenToSms: function(successCallback, errorCallback, abortnumber) {
        cordova.exec(
            successCallback,
            errorCallback,
            'SmsReceiver',
            'registerSMSListener',
            [abortnumber]
        );
    },
    
    stopListening: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'SmsReceiver',
            'unregisterSMSListener',
            []
        );
    }
    
};
module.exports = smsreceiver;
