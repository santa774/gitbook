package com.baban.androidgitbook.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件工具类
 *
 * @author ansen
 * @create time 2015-09-07
 */
public final class FileUtil {
    private static final long MIN_STORAGE = 52428800;//50*1024*1024最低50m
    private static final String TAG = "FileUtil";


    public static String renameFile(String srcPath) {
        String dataStr = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        String targetPath = srcPath.substring(0, srcPath.lastIndexOf(".")) + dataStr + srcPath.substring(srcPath.lastIndexOf("."), srcPath.length());
        File srcDir = new File(srcPath);  //源文件夹路径
        if (srcDir.renameTo(new File(targetPath))) {  // 新文件路径
            return targetPath;
        }
        return srcPath;
    }

    /**
     * 创建文件夹
     *
     * @param path
     */
    public static void makeDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = null;
    }

    public static boolean copyFile(InputStream in, String destFilePath) {
        if (in == null || TextUtils.isEmpty(destFilePath)) {
            return false;
        }
        int len = -1;
        byte[] buffer = new byte[1024];
        FileOutputStream fo = null;
        try {
            File file = new File(destFilePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            fo = new FileOutputStream(file);
            while ((len = in.read(buffer)) != -1) {
                fo.write(buffer, 0, len);
            }
            fo.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null) {
                    fo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //缓存路径
    public static String getCachePath(Context context) {
        String path = getSavePath(context, MIN_STORAGE);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        makeDir(path);
        return path;
    }

    /**
     * 获取保存文件路径
     *
     * @param saveSize 预留空间
     * @return 文件路径
     */
    private static String getSavePath(Context context, long saveSize) {
        String savePath = null;
        if (StorageUtil.getExternaltStorageAvailableSpace() > saveSize) {//扩展存储设备>预留空间
            savePath = StorageUtil.getExternalStorageDirectory();
            File saveFile = new File(savePath);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            } else if (!saveFile.isDirectory()) {
                saveFile.delete();
                saveFile.mkdirs();
            }
        } else if (StorageUtil.getSdcard2StorageAvailableSpace() > saveSize) {//sdcard2外部存储空间>预留空间
            savePath = StorageUtil.getSdcard2StorageDirectory();
            File saveFile = new File(savePath);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            } else if (!saveFile.isDirectory()) {
                saveFile.delete();
                saveFile.mkdirs();
            }
        } else if (StorageUtil.getEmmcStorageAvailableSpace() > saveSize) {//可用的 EMMC 内部存储空间>预留空间
            savePath = StorageUtil.getEmmcStorageDirectory();
            File saveFile = new File(savePath);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            } else if (!saveFile.isDirectory()) {
                saveFile.delete();
                saveFile.mkdirs();
            }
        } else if (StorageUtil.getOtherExternaltStorageAvailableSpace() > saveSize) {//其他外部存储可用空间>预留空间
            savePath = StorageUtil.getOtherExternalStorageDirectory();
            File saveFile = new File(savePath);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            } else if (!saveFile.isDirectory()) {
                saveFile.delete();
                saveFile.mkdirs();
            }
        } else if (StorageUtil.getInternalStorageAvailableSpace(context) > saveSize) {//内部存储目录>预留空间
            savePath = StorageUtil.getInternalStorageDirectory(context) + File.separator;
        }
        return savePath;
    }

    public static String getPathFavourSdcard(Context context) {
        String savePath = "/";
        if (StorageUtil.getOtherExternaltStorageAvailableSpace() > MIN_STORAGE) {//其他外部存储可用空间>预留空间
            try {
                new File("/").exists();
            } catch (NullPointerException e) {
                savePath = StorageUtil.getOtherExternalStorageDirectory();
            }
            File saveFile = new File(savePath);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            } else if (!saveFile.isDirectory()) {
                saveFile.delete();
                saveFile.mkdirs();
            }
        } else {
            savePath = getSavePath(context, MIN_STORAGE);
        }
        return savePath;
    }


    /**
     * 转成正式路径
     *
     * @return
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat) {
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } else {
            return uri2Path(context, uri);
        }


        return null;
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String uri2Path(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index == -1) {
                        index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    }
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Log.e(TAG, "删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e(TAG, "删除单个文件" + fileName + "成功！");
                return true;
            } else {
                Log.e(TAG, "删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            Log.e(TAG, "删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Log.e(TAG, "删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            Log.e(TAG, "删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e(TAG, "删除目录" + dir + "成功！");
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取指定文件大小(单位：字节)
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    /**
     * 将文件大小转换成字节
     */

    public String formatFileSize(long fSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fSize < 1024) {
            fileSizeString = df.format((double) fSize) + "B";
        } else if (fSize > 104875) {
            fileSizeString = df.format((double) fSize / 1024) + "K";
        } else if (fSize > 1073741824) {
            fileSizeString = df.format((double) fSize / 104875) + "M";
        } else {
            fileSizeString = df.format((double) fSize / 1073741824) + "G";
        }
        return fileSizeString;
    }
}