var exec = require('cordova/exec');

exports.VIDEO = 1;
exports.VOICE = 2;
exports.LOCATION = 3;
exports.STORAGE = 4;
exports.NOTICE = 5;
exports.OVERLAYS = 6;

exports.check = function(type, success, error) {
    exec(success, error, 'Permission', 'check', [{type: type}]);
};

exports.checkAll = function (success, error) {
    exec(success, error, 'Permission', 'checkAll', [{}]);
};

exports.openSetting = function (success, error) {
    exec(success, error, 'Permission', 'openSetting', [{}]);
};

exports.openOverlays = function (success, error) {
    exec(success, error, 'Permission', 'openOverlays', [{}]);
};