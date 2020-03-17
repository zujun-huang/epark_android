package cn.epark.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.epark.App;
import cn.epark.BuildConfig;
import cn.epark.R;
import cn.epark.URLConstant;
import cn.epark.bean.ModalBean;
import cn.epark.utils.LogUtil;
import cn.epark.utils.OnMultiClickListener;
import cn.epark.utils.StringUtil;
import cn.epark.view.CircleImageView;

/**
 * Created by huangzujun on 2020/3/17.
 * Describe: 个人资料
 */
public class SetUserInfoActivity extends BaseAct {

    private final int REQUEST_UPDATE_NICKNAME = 0x00000007;
    public static final int REQUEST_TAKE_PHOTO_PERMISSION = 0x00000006;
    private final int REQUEST_EDIT_ICON_CAMERA = 0x00000005;
    private int TAKEPAHTO = 1;// 标识 -> 1:拍照  0:相册
    private final int REQUEST_EDIT_ICON_LIB = 0x00000004;
    private final int REQUEST_EDIT_PIC = 0x00000003;

    private TextView nickname_tv, sex_tv, username_tv, save_tv;
    private CircleImageView head_icon_civ;

    private Activity activity;
    private Uri uriClipUri;
    private byte[] imgBytes;
    private String nickName = "";
    private int gender = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_userinfo);
        activity = this;
        initView();
    }

    private void initView() {
        head_icon_civ = findViewById(R.id.head_icon_civ);
        nickname_tv = findViewById(R.id.nickname_tv);
        sex_tv = findViewById(R.id.sex_tv);
        username_tv = findViewById(R.id.username_tv);
        save_tv = findViewById(R.id.save_tv);
        head_icon_civ.setImageUrl(App.getAccount().getHead());
        nickname_tv.setText(App.getAccount().getNickName());
        sex_tv.setText(App.getAccount().getSex());
        gender = App.getAccount().getGender();
        username_tv.setText(StringUtil.hidePhoneNumber(App.getAccount().getTelphone()));
        nickName = App.getAccount().getNickName();
        findViewById(R.id.head_icon_rl).setOnClickListener(clickListener);
        findViewById(R.id.nickname_rl).setOnClickListener(clickListener);
        findViewById(R.id.sex_rl).setOnClickListener(clickListener);
        findViewById(R.id.save_tv).setOnClickListener(clickListener);
    }

    private void submitUserInfo() {
        HashMap<String, String> params = new HashMap<>(6);
        params.put("user_id", App.getAccount().getId());
        params.put("session_id", App.getAccount().getEncryptionSession());
        params.put("gender", gender + "");
        params.put("name",  nickName);
        params.put("headStr", Base64.encodeToString(imgBytes, Base64.DEFAULT));
        httpPost(App.URL + URLConstant.URL_UPDATE_USER, params, URLConstant.ACTION_UPDATE_USER);
    }

    @Override
    public void onResponseOk(JSONObject data, int actionCode) {
        switch (actionCode) {
            case URLConstant.ACTION_UPDATE_USER:
                //fixme 请求返回系列 意见及个人资料
                handler.obtainMessage(SHOW_TOAST, "个人资料保存成功！").sendToTarget();
                App.getAccount().setNickName(nickName);
                App.getAccount().setGender(gender);
                break;
            default:
                super.onResponseOk(data, actionCode);
        }
    }

    /**
     * 显示没有权限手动设置框
     */
    private void showNoPermissionsToast() {
        showAlertDialog(getString(R.string.modal_dialog_tip), "获取拍摄权限失败,\n请授权后重试~", "去设置", v -> {
            dismiss();
            //用户手动授权
            Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.parse("package:" + context.getPackageName());
            settingIntent.setData(uri);
            startActivity(settingIntent);
        });
    }

    private void showHeadImgModal() {
        List<ModalBean> modalBeanList = new ArrayList<>();
        modalBeanList.add(new ModalBean("拍摄照片", R.color.colorAccent));
        modalBeanList.add(new ModalBean("图库选取", R.color.theme_color));
        showModal(modalBeanList).setOnItemClickListener((parent, view, position, id) -> {
            setSaveVisibility();
            switch (position) {
                case 0:
                    takePhoto();
                    dismissModal();
                    break;
                case 1:
                    TAKEPAHTO = 0;
                    Intent picIntent = new Intent(Intent.ACTION_PICK);
                    picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(picIntent, REQUEST_EDIT_ICON_LIB);
                    dismissModal();
                    break;
                default:
            }
        });
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        TAKEPAHTO = 1;
        // 启动系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (hasSdcard()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// 判断7.0 android系统
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uriClipUri = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".fileProvider",
                        new File(context.getExternalCacheDir(), "icontemp.png"));
            } else {
                uriClipUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "icontemp.png"));
            }
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriClipUri);
        startActivityForResult(intent, REQUEST_EDIT_ICON_CAMERA);// 采用ForResult打开
    }

    /**
     * 判断sdcard是否存在
     */
    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 调用系统的裁剪功能
     *
     * @param uri uri
     */
    public void cropPhoto(Uri uri) {
        if (uri == null) {
            LogUtil.e(TAG, "选择图片Uri不能为空------------");
            showToast("请先选择您想设置的头像后重试~~");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");//系统自带图片裁切功能
        intent.setDataAndType(uri, "image/*");//裁剪的图片uri或图片类型
        intent.putExtra("crop", "true");//设置允许裁剪，circle 圆形剪切
        intent.putExtra("aspectX", 1);//裁剪框的 X 方向的比例,需要为整数
        intent.putExtra("aspectY", 1);//裁剪框的 Y 方向的比例,需要为整数
        intent.putExtra("outputX", 150);//返回数据的时候的X像素大小。
        intent.putExtra("outputY", 150);//返回数据的时候的Y像素大小。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (TAKEPAHTO == 1) {//如果是拍照
                //开启临时访问的读和写权限
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri));
                uriClipUri = uri;
            } else {//如果是相册
                //设置裁剪的图片地址Uri
                uriClipUri = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "clipq.png");
            }
        } else {
            uriClipUri = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "clipq.png");
        }
        LogUtil.d(TAG, "uriClipUri:" + uriClipUri.getPath());
        //Android 对Intent中所包含数据的大小是有限制的，
        // 一般不能超过 1M，否则会使用缩略图,所以我们要指定输出裁剪的图片路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriClipUri);
        intent.putExtra("return-data", false);//是否将数据保留在Bitmap中返回
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出格式，一般设为Bitmap格式及图片类型
        intent.putExtra("noFaceDetection", true);//人脸识别功能
        startActivityForResult(intent, REQUEST_EDIT_PIC);
    }

    private void showSexModal() {
        List<ModalBean> modalBeanList = new ArrayList<>();
        modalBeanList.add(new ModalBean("男", R.color.theme_color));
        modalBeanList.add(new ModalBean("女", R.color.colorAccent));
        showModal(modalBeanList).setOnItemClickListener((parent, view, position, id) -> {
            setSaveVisibility();
            gender = position + 1;
            sex_tv.setText(gender == 1 ? "男" : "女");
            dismissModal();
        });
    }

    private OnMultiClickListener clickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.save_tv:
                    submitUserInfo();
                    break;
                case R.id.sex_rl:
                    showSexModal();
                    break;
                case R.id.head_icon_rl:
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                            showNoPermissionsToast();
                        } else {
                            ActivityCompat.requestPermissions(activity, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA}, REQUEST_TAKE_PHOTO_PERMISSION
                            );
                        }
                    } else {
                        showHeadImgModal();
                    }
                    break;
                case R.id.nickname_rl:
                    startActivityForResult(
                        new Intent(context, SetNickNameActivity.class),
                        REQUEST_UPDATE_NICKNAME
                    );
                    break;
                default:
                    break;
            }
        }
    };

    private void setSaveVisibility() {
        if (save_tv.getVisibility() != View.VISIBLE) {
            save_tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_TAKE_PHOTO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                showHeadImgModal();
            } else {
                showNoPermissionsToast();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_UPDATE_NICKNAME:
                    if (data != null) {
                        nickName = data.getStringExtra("name");
                        nickname_tv.setText(nickName);
                        setSaveVisibility();
                    }
                    break;
                case REQUEST_EDIT_ICON_LIB:
                    dismissModal();
                    cropPhoto(data.getData());// 裁剪图片
                    break;
                case REQUEST_EDIT_ICON_CAMERA:
                    if (hasSdcard()) {
                        cropPhoto(uriClipUri);
                    } else {
                        showToast("未找到存储卡，无法存储照片！");
                    }
                    break;
                case REQUEST_EDIT_PIC:
                    try {
                        Bitmap headicon = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uriClipUri));
                        if (headicon != null) {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            headicon.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            imgBytes = bos.toByteArray();
                            head_icon_civ.setImageBitmap(headicon);
                        } else {
                            LogUtil.e(TAG, "Bitmap headicon 为空！-------------");
                            showToast("请先选取图片后重试~~");
                        }
                    } catch (FileNotFoundException e) {
                        LogUtil.e(TAG, "文件 uriClipUri：" + uriClipUri + "未找到！-------");
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
