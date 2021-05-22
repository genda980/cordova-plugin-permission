var exec = require('cordova/exec');

// *** 重点
    // plugin.xml 文件中 <clobbers 里面的值 改为 PPermission 这个是 js的调用名 （为了区分）
// ***

exports.VIDEO = 1;          // 相机、麦克风、存储
exports.VOICE = 2;          // 麦克风、存储
exports.LOCATION = 3;       // 地理位置
exports.STORAGE = 4;        // 存储
exports.NOTICE = 5;         // 通知
exports.OVERLAYS = 6;       // 仅Android

// 申请权限 type 为上面的值
// 返回  success 不需要返回参数
// 返回  fail 参数 0 -> 用户点击了 "拒绝"
//                1 -> 用户已经拒绝过（好处理引导用户开启）
exports.check = function(type, success, error) {
    exec(success, error, 'Permission', 'check', [{type: type}]);
};

// 检测全部需要的权限 可以先不处理
// notice
// camera
// voice
// storage
// location
// drawOverlays     仅Android
exports.checkAll = function (success, error) {
    exec(success, error, 'Permission', 'checkAll', [{}]);
};

// 打开权限设置页面
exports.openSetting = function (success, error) {
    exec(success, error, 'Permission', 'openSetting', [{}]);
};

// 仅Android
exports.openOverlays = function (success, error) {
    exec(success, error, 'Permission', 'openOverlays', [{}]);
};