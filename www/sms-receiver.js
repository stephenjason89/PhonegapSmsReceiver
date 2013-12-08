var smsreceiver = {
    
    listenToSms: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
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