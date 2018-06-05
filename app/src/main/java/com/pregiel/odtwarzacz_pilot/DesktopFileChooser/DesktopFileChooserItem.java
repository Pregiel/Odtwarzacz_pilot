package com.pregiel.odtwarzacz_pilot.DesktopFileChooser;


public class DesktopFileChooserItem implements DesktopFileChooserItemInterface {
    private String path;
    private boolean directory;

    public DesktopFileChooserItem(String path) {
        this.path = path;
        if (path.lastIndexOf(".") > 0) {
            directory = false;
        } else {
            directory = true;
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getName() {
        String[] s = path.split("\\\\");

        return s[s.length - 1];
    }

    @Override
    public boolean isDirectory() {
        return directory;
    }

    @Override
    public String getExtension() {
        return path.substring(path.lastIndexOf(".") + 1);
    }

}
