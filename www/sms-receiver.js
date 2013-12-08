var smsreceiver = {
    
    listenToSms: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'SMSReceiver',
            'registerSMSListener',
            []
        );
    },
    
    stopListening: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'SMSReceiver',
            'unregisterSMSListener',
            []
        );
    }
    
};
module.exports = smsreceiver;