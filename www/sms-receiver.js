var smsreceiver = {
    
    listenToSms: function(successCallback, errorCallback, testabort) {
        cordova.exec(
            successCallback,
            errorCallback,
            testabort,
            'SmsReceiver',
            'registerSMSListener',
            []
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
