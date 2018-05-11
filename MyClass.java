package com.example.lib;

import com.google.common.collect.Ordering;

import org.jf.baksmali.Baksmali;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedMethod;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.smali.Smali;
import org.jf.util.ClassFileNameHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class MyClass {
    public static void disassemble(DexBackedDexFile dexBackedDexFile,File outfile){
        int jobs=Runtime.getRuntime().availableProcessors();
        BaksmaliOptions options = new BaksmaliOptions();
        List<String> classes = null;
        Baksmali.disassembleDexFile(dexBackedDexFile,outfile,jobs,options);

    }

    public static void disassebleclass(DexBackedDexFile dexBackedDexFile,File outputDir,final BaksmaliOptions options){
        //dexBackedDexFile.getClasses();
        List<? extends ClassDef> classDefs = Ordering.natural().sortedCopy(dexBackedDexFile.getClasses());

        final ClassFileNameHandler fileNameHandler = new ClassFileNameHandler(outputDir, ".smali");
        for(DexBackedClassDef dexclass:dexBackedDexFile.getClasses()){
            try {
                Method method=Baksmali.class.getDeclaredMethod("disassembleClass",new Class[]{ClassDef.class,ClassFileNameHandler.class,BaksmaliOptions.class});
                method.setAccessible(true);
                method.invoke(null,new Object[]{dexclass,fileNameHandler,options});
                print(dexclass.getType());//可以得到类的具体路径，从而可以对反编译的类进行筛选。

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }


    public static void main(String[] args){
        String apkpath="/Users/yixianglin/Desktop/test/test.apk";//apk文件路径
        String zippath="/Users/yixianglin/Desktop/test/out";//解压apk得到的文件路径
        String smalidir="/Users/yixianglin/Desktop/test";//对dex反编译后存放的目录
        String classdir="/Users/yixianglin/Desktop/test/class";
        BaksmaliOptions baksmaliOptions=new BaksmaliOptions();
        File file=new File(smalidir);
        if(!file.exists()){
            file.mkdir();
        }
        try {
            ZipUtils.unZipFiles(new File(apkpath),zippath);//对apk文件进行解压，拿到dex文件路径
            File zipfile=new File(zippath);
            for(File zf:zipfile.listFiles()){
                if(zf.getName().trim().endsWith("dex")){
                    String dexname=zf.getName();
                    String dexpath=zf.getAbsolutePath();
                    String smali_file_path=smalidir+File.separator+dexname.replace(".","_");
                    File smali_file=new File(smali_file_path);
                    if(!smali_file.exists()){
                        smali_file.mkdirs();
                    }
                    DexBackedDexFile dexBackedDexFile=DexFileFactory.loadDexFile(dexpath,Opcodes.forApi(15));
                    //disassemble(dexBackedDexFile,smali_file);
                    File class_file=new File(classdir);
                    if(!class_file.exists()){
                        class_file.mkdirs();
                    }
                    disassebleclass(dexBackedDexFile,class_file,baksmaliOptions);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void print(Object obj){
        System.out.println(obj);
    }

}
