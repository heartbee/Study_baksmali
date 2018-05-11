package com.example.lib;

import com.google.common.base.Strings;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.MultiDexContainer;

import java.io.File;
import java.io.IOException;

public class HandleAPK {
    protected File inputFile;
    protected String inputEntry;
    protected DexBackedDexFile dexFile;
    public int apiLevel = 15;

    public  DexBackedDexFile getDexFileObject(String input) {
        File file = new File(input);
        while ((file != null) && (!file.exists())) {
            file = file.getParentFile();
        }
        if ((file == null) || (!file.exists()) || (file.isDirectory())) {
            System.err.println("Can't find file: " + input);
            System.exit(1);
        }
        this.inputFile = file;

        String dexEntry = null;
        if (file.getPath().length() < input.length()) {
            dexEntry = input.substring(file.getPath().length() + 1);
        }
        if (!Strings.isNullOrEmpty(dexEntry)) {
            boolean exactMatch = false;
            if ((dexEntry.length() > 2) && (dexEntry.charAt(0) == '"') && (dexEntry.charAt(dexEntry.length() - 1) == '"')) {
                dexEntry = dexEntry.substring(1, dexEntry.length() - 1);
                exactMatch = true;
            }
            this.inputEntry = dexEntry;
            try {
                this.dexFile = DexFileFactory.loadDexEntry(file, dexEntry, exactMatch, Opcodes.forApi(this.apiLevel));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            try {
                this.dexFile = DexFileFactory.loadDexFile(file, Opcodes.forApi(this.apiLevel));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return dexFile;

    }

}
