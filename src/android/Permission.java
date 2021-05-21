package org.apache.cordova.permission;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.qw.soul.permission.callbcak.GoAppDetailCallBack;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.NotificationManagerCompat;

/**
 * This class echoes a string called from JavaScript.
 */
public class Permission extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject jsonObject = new JSONObject(args.optString(0, ""));
        if (action.equals("check")) {
            check(jsonObject, callbackContext);
            return true;
        }

        if (action.equals("checkAll")) {
            checkAll(callbackContext);
            return true;
        }

        if (action.equals("openSetting")) {
            openSetting();
            return true;
        }

        if (action.equals("openOverlays")) {
            openOverlays();
            return true;
        }
        return false;
    }

    private void check(JSONObject obj, CallbackContext callbackContext) {
        int type = obj.optInt("type", 0);
        if (type == 0) {
            callbackContext.error("params error");
            return;
        }
        String[] permissionStrArr = null;
        if (type == 1) {
            permissionStrArr = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else if (type == 2) {
            permissionStrArr = new String[]{Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else if (type == 3) {
            permissionStrArr = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        } else if (type == 4) {
            permissionStrArr = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        if (permissionStrArr != null) {
            checkPermission(permissionStrArr, callbackContext);
            return;
        }
        if (type == 5) {
            boolean isNoticeOpen = NotificationManagerCompat.from(cordova.getContext()).areNotificationsEnabled();
            callbackContext.success(isNoticeOpen ? 1 : 0);
            return;
        }
        if (type == 6) {
            callbackContext.success(hasDrawOverlays() ? 1 : 0);
        }
    }

    private void checkAll(CallbackContext callbackContext) {
        try {
            boolean noticeOpen = NotificationManagerCompat.from(cordova.getContext()).areNotificationsEnabled();
            com.qw.soul.permission.bean.Permission[] camera = SoulPermission.getInstance().checkPermissions(Manifest.permission.CAMERA);
            com.qw.soul.permission.bean.Permission[] voice = SoulPermission.getInstance().checkPermissions(Manifest.permission.RECORD_AUDIO);
            com.qw.soul.permission.bean.Permission[] storage = SoulPermission.getInstance().checkPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            com.qw.soul.permission.bean.Permission[] location = SoulPermission.getInstance().checkPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
            boolean drawOverlays = hasDrawOverlays();
            boolean cameraOpen = camera[0].isGranted();
            boolean voiceOpen = voice[0].isGranted();
            boolean storageOpen = storage[0].isGranted() && storage[1].isGranted();
            boolean locationOpen = location[0].isGranted() && location[1].isGranted();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("notice", noticeOpen);
            jsonObject.put("camera", cameraOpen);
            jsonObject.put("voice", voiceOpen);
            jsonObject.put("storage", storageOpen);
            jsonObject.put("location", locationOpen);
            jsonObject.put("drawOverlays", drawOverlays);
            callbackContext.success(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openSetting() {
        SoulPermission.getInstance().goApplicationSettings(new GoAppDetailCallBack() {
            @Override
            public void onBackFromAppDetail(Intent data) {
            }
        });
    }

    private void openOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cordova.getContext().startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + cordova.getContext().getPackageName())));
        }
    }

    private void checkPermission(String[] permissionStrArr, CallbackContext callbackContext) {
        SoulPermission.getInstance().checkAndRequestPermissions(Permissions.build(permissionStrArr), new CheckRequestPermissionsListener() {
            @Override
            public void onAllPermissionOk(com.qw.soul.permission.bean.Permission[] allPermissions) {
                callbackContext.success();
            }

            @Override
            public void onPermissionDenied(com.qw.soul.permission.bean.Permission[] refusedPermissions) {
                for (int i = 0; i < refusedPermissions.length; i++) {
                    com.qw.soul.permission.bean.Permission permission = refusedPermissions[0];
                    if (!permission.shouldRationale()) {
                        callbackContext.error(1);
                        return;
                    }
                }
                callbackContext.error(0);
            }
        });
    }

    private boolean hasDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(cordova.getContext());
        }
        return true;
    }
}
