package com.poratu.idea.plugins.tomcat.setting;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.poratu.idea.plugins.tomcat.utils.PluginUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * Author : zengkid
 * Date   : 2017-02-25
 * Time   : 13:40
 */
public class TomcatSetting {
    private JPanel mainPanel;
    private JList tomcatList;
    private LabeledComponent<JTextField> tomcatPath;
    private LabeledComponent<JTextField> tomcatName;
    private JPanel tomcatListPanel;
    private JPanel tomcatSetupPanel;
    private JTextField tomcatNameField;
    private JTextField tomcatVersionField;
    private TextFieldWithBrowseButton tomcatServerField;
    private static TomcatSetting tomcatSetting = new TomcatSetting();

    private boolean inited = false;

    private TomcatSetting() {

    }

    public static TomcatSetting getInstance() {
        return tomcatSetting;
    }

    public void initComponent() {
        if (!inited) {

            ToolbarDecorator decorator = ToolbarDecorator.createDecorator(tomcatList).setAsUsualTopToolbar();
            decorator.setAddAction(new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {

                    DefaultListModel<TomcatInfo> model = (DefaultListModel<TomcatInfo>) tomcatList.getModel();

                    VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
                    if (virtualFile == null) { // cancel to choose file
                        return;
                    }
                    String presentableUrl = virtualFile.getPresentableUrl();

                    Sdk[] allJdks = ProjectJdkTable.getInstance().getAllJdks();
                    if (allJdks == null || allJdks.length == 0) {
                        //todo: guide user to config the SDK
                        return;

                    }
                    Sdk jdk = allJdks[0];


                    TomcatInfo tomcatInfo = PluginUtils.getTomcatInfo(jdk.getHomePath(), presentableUrl);
                    int size = model.size();
                    if (model.contains(tomcatInfo)) {
                        TomcatInfo[] infos = new TomcatInfo[size];
                        model.copyInto(infos);
                        Optional<TomcatInfo> max = Arrays.stream(infos).filter(it -> it.equals(tomcatInfo)).max(Comparator.comparingInt(TomcatInfo::getNumber));
                        tomcatInfo.setNumber(max.get().getNumber() + 1);
                    }
                    model.add(size, tomcatInfo);
                    tomcatList.setSelectedIndex(size);


                }
            });

            tomcatListPanel.add(decorator.createPanel(), BorderLayout.CENTER);
            tomcatList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        TomcatInfo tomcatInfo = (TomcatInfo) tomcatList.getSelectedValue();
                        if (tomcatInfo != null) {
                            tomcatNameField.setText(tomcatInfo.getName());
                            tomcatVersionField.setText(tomcatInfo.getVersion());
                            tomcatServerField.setText(tomcatInfo.getPath());
                        } else {
                            tomcatNameField.setText("");
                            tomcatVersionField.setText("");
                            tomcatServerField.setText("");
                        }
                    }

                }
            });

            inited = true;
        }

        DefaultListModel<TomcatInfo> model = new DefaultListModel<>();
        java.util.List<TomcatInfo> tomcatInfos = TomcatInfoConfigs.getInstance().getTomcatInfos();
        for (TomcatInfo tomcatInfo : tomcatInfos) {
            model.add(model.size(), tomcatInfo);
        }
        tomcatList.setModel(model);
        tomcatNameField.setText("");
        tomcatVersionField.setText("");
        tomcatServerField.setText("");
        if (tomcatList.getModel().getSize() > 0) {
            tomcatList.setSelectedIndex(0);
        }


    }


    public JPanel getMainPanel() {
        initComponent();
        return mainPanel;
    }

    public JList getTomcatList() {
        return tomcatList;
    }

    public LabeledComponent<JTextField> getTomcatPath() {
        return tomcatPath;
    }

    public LabeledComponent<JTextField> getTomcatName() {
        return tomcatName;
    }

}
