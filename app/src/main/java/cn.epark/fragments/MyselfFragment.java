package cn.epark.fragments;


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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.epark.App;
import cn.epark.BuildConfig;
import cn.epark.R;
import cn.epark.activitys.MainActivity;
import cn.epark.activitys.NoticeActivity;
import cn.epark.activitys.SMSLoginActivity;
import cn.epark.bean.ModalBean;
import cn.epark.utils.LogUtil;
import cn.epark.utils.ShareUtil;
import cn.epark.utils.ThreadUtil;
import cn.epark.view.CircleImageView;

/**
 * @作者 hzj
 * @日期 2019/7/16
 * @描述 我的 Fragment
 */
public class MyselfFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_EDIT_ICON_CAMERA = 0x000700;
    private static int TAKEPAHTO = 1;// 标识 -> 1:拍照  0:相册
    private static final int REQUEST_EDIT_ICON_LIB = 0x000770;
    private static final int REQUEST_EDIT_PIC = 0x000790;
    public static final int REQUEST_LOGIN = 0x000791;
    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 0x000799;//申请权限

    private MainActivity mainAct;
    private CircleImageView head_img;//头像
    private TextView userNameTv;

    private Uri uriClipUri;

    public static MyselfFragment newInstance() {
        MyselfFragment fragment = new MyselfFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_NAME, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_NAME);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        mainAct = (MainActivity) getActivity();


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        head_img = view.findViewById(R.id.head_img);
        head_img.setOnClickListener(this);
        view.findViewById(R.id.notify_my).setOnClickListener(this);
        userNameTv = view.findViewById(R.id.nick_name);
        userNameTv.setOnClickListener(this);
        view.findViewById(R.id.rl_qd).setOnClickListener(this);
        view.findViewById(R.id.tv_wallet).setOnClickListener(this);
        view.findViewById(R.id.rl_zd).setOnClickListener(this);
        view.findViewById(R.id.rl_car).setOnClickListener(this);
        view.findViewById(R.id.tv_coupon).setOnClickListener(this);
        view.findViewById(R.id.tv_collection).setOnClickListener(this);
        view.findViewById(R.id.tv_set).setOnClickListener(this);
    }

    private void initData() {
        String headImg = ShareUtil.newInstance().getLocHeadImg(context);
        if (!TextUtils.isEmpty(headImg)) {
            byte[] bytes = Base64.decode(headImg, Base64.DEFAULT);
            head_img.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notify_my://通知
                startActivity(new Intent(context, NoticeActivity.class));
                break;
            case R.id.head_img://头像
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mainAct, Manifest.permission.CAMERA)) {
                        showNoPermissionsToast();
                    } else {
                        ActivityCompat.requestPermissions(mainAct, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA}, REQUEST_TAKE_PHOTO_PERMISSION
                        );
                    }
                } else {
                    showHeadImgModal();
                }
                break;
            case R.id.nick_name://昵称
                if (TextUtils.isEmpty(App.getAccount().getNickName())) {
                    startActivity(new Intent(context, SMSLoginActivity.class));
                } else {
                    //TODO 修改昵称
                }
                break;
            case R.id.rl_qd://每日签到
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.daily_attendance))
                );
                break;
            case R.id.tv_wallet://我的钱包
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.my_wallet))
                );
                break;
            case R.id.rl_zd://停车记录
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.parking_record))
                );
                break;
            case R.id.rl_car://车辆管理
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.vehicle_management))
                );
                break;
            case R.id.tv_coupon://优惠券
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.my_coupon))
                );
                break;
            case R.id.tv_collection://我的收藏
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.my_collection))
                );
                break;
            case R.id.tv_set://设置
                startActivity(new Intent(context, NoticeActivity.class)
                        .putExtra("title", getString(R.string.my_setting))
                );
                break;
            default:
        }
    }

    private void showHeadImgModal() {
        List<ModalBean> modalBeanList = new ArrayList<>();
        modalBeanList.add(new ModalBean("拍摄照片", R.color.colorAccent));
        modalBeanList.add(new ModalBean("图库选取", R.color.theme_color));
        showModal(modalBeanList).setOnItemClickListener((parent, view, position, id) -> {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
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
                            uploadImage(bos.toByteArray());
                            head_img.setImageBitmap(headicon);
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

    private void updateUserInfo() {
        Glide.with(context)
                .load(App.getAccount().getHead())
                .error(ContextCompat.getDrawable(context, R.mipmap.default_icon))
                .into(head_img);
        if (!TextUtils.isEmpty(App.getAccount().getNickName())) {
            userNameTv.setText(App.getAccount().getNickName());
        }

    }

    private void uploadImage(byte[] bytes) {
        ThreadUtil.runInThread(() ->
                //由于未有服务器暂且本地存储
                ShareUtil.newInstance().getShared(context, ShareUtil.USER_HEAD_IMG).edit()
                        .putString(ShareUtil.USER_HEAD_IMG, Base64.encodeToString(bytes, Base64.DEFAULT)).apply());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_TAKE_PHOTO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                showHeadImgModal();
            } else {
                //手动设置
                showNoPermissionsToast();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }
}
