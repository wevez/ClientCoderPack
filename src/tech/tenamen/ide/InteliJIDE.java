package tech.tenamen.ide;

import tech.tenamen.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InteliJIDE extends IDE {

    private static File PROPERTY_DIR, IDE_LIBRARIES_DIR;

    @Override
    public void createProperties() {
        PROPERTY_DIR = new File(Main.WORKING_DIR, ".idea");
        PROPERTY_DIR.mkdirs();
        IDE_LIBRARIES_DIR = new File(PROPERTY_DIR, "libraries");
        IDE_LIBRARIES_DIR.mkdirs();
        // .gitignore
        System.out.println("Making .gitignore");
        this.makeGitignore();
        // .name
        System.out.println("Making .name");
        this.makeName();
        // misc.xml
        System.out.println("Making misc.xml");
        this.makeMisc();
        // modules.xml
        System.out.println("Making modules.xml");
        this.makeModules();
        // vcs.xml
        System.out.println("Making vcs.xml");
        this.makeVCS();
        // workspace.xml
        System.out.println("Making workspace.xml");
        this.makeWorkspace();
        // libraries
        System.out.println("Making libraries");
        this.makeLibraries();
        // iml
        System.out.println("Making iml");
        this.makeIml();
    }

    private void makeGitignore() {
        final File gitignoreFile = new File(PROPERTY_DIR, ".gitignore");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gitignoreFile);
            writer.write("# Default ignored files\n/shelf/\n/workspace.xml\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeName() {
        final File gitignoreFile = new File(PROPERTY_DIR, ".name");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gitignoreFile);
            writer.write(Main.WORKING_DIR.getName());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeMisc() {
        final File gitignoreFile = new File(PROPERTY_DIR, "misc.xml");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gitignoreFile);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<project version=\"4\">\n");
            writer.write(String.format("  <component name=\"ProjectRootManager\" version=\"2\" languageLevel=\"JDK_%d\" default=\"true\" project-jdk-name=\"%d\" project-jdk-type=\"JavaSDK\">\n",
                    Main.client.getJavaMajorVersion(), Main.client.getJavaMajorVersion()));
            writer.write("    <output url=\"file://$PROJECT_DIR$/out\" />\n");
            writer.write("  </component>\n");
            writer.write("</project>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeModules() {
        final File gitignoreFile = new File(PROPERTY_DIR, "modules.xml");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gitignoreFile);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<project version=\"4\">\n");
            writer.write(" <component name=\"ProjectModuleManager\">\n");
            writer.write("    <modules>\n");
            writer.write(String.format("      <module fileurl=\"file://$PROJECT_DIR$/%s.iml\" filepath=\"$PROJECT_DIR$/%s.iml\" />\n",
                    Main.WORKING_DIR.getName(), Main.WORKING_DIR.getName()));
            writer.write("    </modules>\n");
            writer.write("  </component>\n");
            writer.write("</project>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeVCS() {
        final File gitignoreFile = new File(PROPERTY_DIR, "vcs.xml");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gitignoreFile);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<project version=\"4\">\n");
            writer.write("  <component name=\"VcsDirectoryMappings\">\n");
            writer.write("    <mapping directory=\"\" vcs=\"Git\" />\n");
            writer.write("    <mapping directory=\"$PROJECT_DIR$\" vcs=\"Git\" />\n");
            writer.write("  </component>\n");
            writer.write("</project>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeWorkspace() {
        final File gitignoreFile = new File(PROPERTY_DIR, "workspace.xml");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gitignoreFile);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<project version=\"4\">\n");
            writer.write("</project>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeLibraries() {
        final File gitignoreFile = new File(IDE_LIBRARIES_DIR, "ccp.xml");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gitignoreFile);
            writer.write("<component name=\"libraryTable\">\n");
            writer.write("  <library name=\"ccp\">\n");
            writer.write("    <CLASSES>\n");
            for (String libraryName : this.getLibraryNames()) {
                writer.write(String.format("      <root url=\"jar://$PROJECT_DIR$/libraries/%s!/\" />\n", libraryName));
            }
            writer.write("    </CLASSES>\n");
            writer.write("    <JAVADOC />\n");
            writer.write("    <NATIVE>\n");
            writer.write("      <root url=\"file://$PROJECT_DIR$/libraries/natives\" />\n");
            writer.write("    </NATIVE>\n");
            writer.write("    <SOURCES />\n");
            writer.write("  </library>\n");
            writer.write("</component>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeIml() {
        final File gitignoreFile = new File(Main.WORKING_DIR, String.format("%s.iml", Main.WORKING_DIR.getName()));
        FileWriter writer = null;
        try {
            writer = new FileWriter(gitignoreFile);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<module type=\"JAVA_MODULE\" version=\"4\">\n");
            writer.write("  <component name=\"NewModuleRootManager\" inherit-compiler-output=\"true\">\n");
            writer.write("    <exclude-output />\n");
            writer.write("    <content url=\"file://$MODULE_DIR$\">\n");
            writer.write("      <sourceFolder url=\"file://$MODULE_DIR$/src\" isTestSource=\"false\" />\n");
            writer.write("    </content>\n");
            writer.write("    <orderEntry type=\"inheritedJdk\" />\n");
            writer.write("    <orderEntry type=\"sourceFolder\" forTests=\"false\" />\n");
            writer.write("    <orderEntry type=\"library\" name=\"ccp\" level=\"project\" />\n");
            writer.write("  </component>\n");
            writer.write("</module>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
