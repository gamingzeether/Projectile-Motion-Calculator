import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main {
    // UI components
    static JFrame mainFrame;
    static ImagePanel outputImagePanelSolutions;
    static JLabel outputLabelSolution;
    static JTextField inputLaunchX;
    static JTextField inputLaunchY;
    static JTextField inputLaunchZ;
    static JTextField inputTargetX;
    static JTextField inputTargetY;
    static JTextField inputTargetZ;
    static ArrayList<JTextField> inputFields;

    static double[] chargeSpeeds;
    static double GRAVITY = 9.8;
    static int DISPLAY_MARGIN = 50; // Margin in pixels

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Main::createGui);
    }
    private static void createGui() {
        inputFields = new ArrayList<>();
        mainFrame = new JFrame();
        Container contentPane = mainFrame.getContentPane();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel1 = new JPanel(new GridBagLayout());
        contentPane.add(panel1);
        GridBagConstraints c;

        ActionListener verifyTextAction = e -> {
            JTextField textField = (JTextField) e.getSource();
            verifyTextInputActionListener(textField, true);
        };
        FocusAdapter verifyTextFocus = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                JTextField textField = (JTextField) e.getSource();
                verifyTextInputActionListener(textField, true);
            }
        };

        //region Trajectory display
        int displayWidth = 1000;
        int displayHeight = 500;
        ImagePanel imagePanel1 = new ImagePanel(displayWidth, displayHeight);
        outputImagePanelSolutions = imagePanel1;
        imagePanel1.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                imagePanel1.onMouseMove(e.getX(), e.getY());
            }
        });
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = displayWidth;
        c.ipady = displayHeight;
        c.gridwidth = 2;
        c.gridheight = 20;
        c.gridx = 0;
        c.gridy = 0;
        panel1.add(imagePanel1, c);

        JButton button1 = new JButton();
        button1.setText("Calculate Solutions");
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = 200;
        c.gridx = 0;
        c.gridy = 20;
        button1.addActionListener(e -> doCalculation());
        panel1.add(button1, c);

        JLabel labelSolutionInfo = new JLabel();
        labelSolutionInfo.setText("Firing Solution: ");
        c.gridwidth = 2;
        c.gridy++;
        panel1.add(labelSolutionInfo, c);
        outputLabelSolution = labelSolutionInfo;

        JButton buttonReset = new JButton();
        buttonReset.setText("Reset");
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 20;
        buttonReset.addActionListener(e -> resetThings());
        panel1.add(buttonReset, c);
        //endregion

        //region Data input (Launch & target positions)
        JLabel labelLaunchPos = new JLabel();
        labelLaunchPos.setText("Launch Position XYZ (m)");
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = 100;
        c.gridx = 2;
        c.gridy = 0;
        panel1.add(labelLaunchPos, c);

        JTextField textField1 = new JTextField();
        inputLaunchX = textField1;
        textField1.addActionListener(verifyTextAction);
        textField1.addFocusListener(verifyTextFocus);
        textField1.setText("0");
        c.gridy++;
        panel1.add(textField1, c);

        JTextField textField2 = new JTextField();
        inputLaunchY = textField2;
        textField2.addActionListener(verifyTextAction);
        textField2.addFocusListener(verifyTextFocus);
        textField2.setText("0");
        c.gridy++;
        panel1.add(textField2, c);

        JTextField textField3 = new JTextField();
        inputLaunchZ = textField3;
        textField3.addActionListener(verifyTextAction);
        textField3.addFocusListener(verifyTextFocus);
        textField3.setText("0");
        c.gridy++;
        panel1.add(textField3, c);

        JLabel labelTargetPos = new JLabel();
        labelTargetPos.setText("Target Position XYZ (m)");
        c.gridy++;
        panel1.add(labelTargetPos, c);

        JTextField textField4 = new JTextField();
        inputTargetX = textField4;
        textField4.addActionListener(verifyTextAction);
        textField4.addFocusListener(verifyTextFocus);
        textField4.setText("0");
        c.gridy++;
        panel1.add(textField4, c);

        JTextField textField5 = new JTextField();
        inputTargetY = textField5;
        textField5.addActionListener(verifyTextAction);
        textField5.addFocusListener(verifyTextFocus);
        textField5.setText("0");
        c.gridy++;
        panel1.add(textField5, c);

        JTextField textField6 = new JTextField();
        inputTargetZ = textField6;
        textField6.addActionListener(verifyTextAction);
        textField6.addFocusListener(verifyTextFocus);
        textField6.setText("0");
        c.gridy++;
        panel1.add(textField6, c);

        inputFields.add(textField1);
        inputFields.add(textField2);
        inputFields.add(textField3);
        inputFields.add(textField4);
        inputFields.add(textField5);
        inputFields.add(textField6);
        //endregion

        //region Data input (Projectile data)
        JLabel labelProjectileSpeeds = new JLabel();
        labelProjectileSpeeds.setText("Propellant Charge Speeds (m/s)");
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = 150;
        c.gridx = 3;
        c.gridy = 0;
        panel1.add(labelProjectileSpeeds, c);

        // Text fields for propellant charge amounts -> projectile speed
        int maxChargeCount = 9;
        chargeSpeeds = new double[maxChargeCount];
        for (int i = 1; i <= maxChargeCount; i++) {
            JTextField textFieldChargeSpeed = new JTextField();
            textFieldChargeSpeed.setName("TextFieldChargeSpeed" + i);
            textFieldChargeSpeed.setText("0");
            textFieldChargeSpeed.addActionListener(e -> chargeSpeedTextAction(textFieldChargeSpeed));
            textFieldChargeSpeed.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    chargeSpeedTextAction(textFieldChargeSpeed);
                }
            });
            c.gridy = i;
            panel1.add(textFieldChargeSpeed, c);
            inputFields.add(textFieldChargeSpeed);
        }
        //endregion

        //region Data input (Weather)

        //endregion

        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    private static void doCalculation() {
        //region Get inputs
        Vector3 launchPos = new Vector3(0, 0, 0);
        Vector3 targetPos = new Vector3(0, 0, 0);

        launchPos.x = Double.parseDouble(inputLaunchX.getText());
        launchPos.y = Double.parseDouble(inputLaunchY.getText());
        launchPos.z = Double.parseDouble(inputLaunchZ.getText());

        targetPos.x = Double.parseDouble(inputTargetX.getText());
        targetPos.y = Double.parseDouble(inputTargetY.getText());
        targetPos.z = Double.parseDouble(inputTargetZ.getText());
        //endregion

        //region Calculate firing solutions
        ArrayList<FiringSolution> solutions = FiringSolution.calculateSolutions(launchPos, targetPos, chargeSpeeds);
        //endregion

        //region Display firing solutions
        outputImagePanelSolutions.clearShapes();
        double metersPerPixel = 1;
        Vector2Int launchPosScreen;
        Vector2Int targetPosScreen;

        // Calculate positions on screen
        Vector3 targetOffset = targetPos.diff(launchPos);
        int pixelsX = outputImagePanelSolutions.pixelWidth - 2 * DISPLAY_MARGIN;
        int pixelsY = outputImagePanelSolutions.pixelHeight - 2 * DISPLAY_MARGIN;
        double maxTrajectoryHeight = 0;
        for (FiringSolution s : solutions) {
            double maxHeight = s.maxHeight();
            if (maxHeight > maxTrajectoryHeight)
                maxTrajectoryHeight = maxHeight;
        }
        maxTrajectoryHeight += launchPos.z;
        double distHoriz = Math.sqrt(targetOffset.x * targetOffset.x + targetOffset.y * targetOffset.y);
        double zDiff = Math.max(maxTrajectoryHeight, Math.max(launchPos.z, targetPos.z)) - Math.min(launchPos.z, targetPos.z);
        metersPerPixel = Math.max(distHoriz / pixelsX, Math.abs(zDiff) / pixelsY);

        launchPosScreen = new Vector2Int(0, 0);
        targetPosScreen = new Vector2Int((int)(distHoriz / metersPerPixel), (int)(targetOffset.z / metersPerPixel));
        int minX = Math.min(launchPosScreen.x, targetPosScreen.x);
        int minY = Math.min(launchPosScreen.y, targetPosScreen.y);
        launchPosScreen.x = launchPosScreen.x - minX + DISPLAY_MARGIN;
        targetPosScreen.x = targetPosScreen.x - minX + DISPLAY_MARGIN;
        launchPosScreen.y = launchPosScreen.y - minY + DISPLAY_MARGIN;
        targetPosScreen.y = targetPosScreen.y - minY + DISPLAY_MARGIN;

        // Draw launch and target icons
        outputImagePanelSolutions.addShape(new Circle(targetPosScreen, 20, Color.orange));
        outputImagePanelSolutions.addShape(new Circle(launchPosScreen, 20, Color.blue));

        // Draw trajectory
        for (FiringSolution f : solutions) {
            outputImagePanelSolutions.addShape(new Trajectory(f, launchPosScreen, metersPerPixel));
        }

        // Draw lines to show scale
        int scaleSigFigs = (int)(Math.floor(Math.log10(distHoriz)));
        int scaleMaxDist = (int)(Math.pow(10, scaleSigFigs));
        int bottomLineY = 10;
        Vector2Int bottomLineStart = new Vector2Int(launchPosScreen.x, bottomLineY);
        Vector2Int bottomLineEnd = new Vector2Int(launchPosScreen.x + (int)(Math.pow(10, scaleSigFigs) / metersPerPixel), bottomLineY);
        outputImagePanelSolutions.addShape(new Line(bottomLineStart , bottomLineEnd, 2, Color.black));
        for (int i = 1; i <= scaleSigFigs; i++) {
            double dist = Math.pow(10, i);
            Vector2Int lineStart = new Vector2Int(launchPosScreen.x + (int)(dist / metersPerPixel), bottomLineY);
            Vector2Int lineEnd = new Vector2Int(lineStart.x, lineStart.y + 10);
            outputImagePanelSolutions.addShape(new Line(lineStart, lineEnd, 2, Color.black));
        }
        Vector2Int scaleNumberPos = new Vector2Int(bottomLineEnd.x + 10, bottomLineEnd.y);
        Font scaleNumberFont = Font.getFont(Font.SANS_SERIF);
        String scaleUnit = " m";
        if (scaleMaxDist >= 1000) {
            scaleMaxDist /= 1000;
            scaleUnit = " Km";
        }
        outputImagePanelSolutions.addShape(new Text(scaleNumberPos,  scaleMaxDist + scaleUnit, scaleNumberFont, Color.black));

        // Add compass
        double compassRadiusOuter = 50;
        double compassRadiusInner = 47;
        Vector2Int compassCenter = new Vector2Int(outputImagePanelSolutions.pixelWidth - (int)compassRadiusOuter - 15, outputImagePanelSolutions.pixelHeight - (int)compassRadiusOuter - 20);
        outputImagePanelSolutions.addShape(new Text(compassCenter.add(-5, (int)compassRadiusOuter + 10), "N", Font.getFont(Font.SANS_SERIF), Color.black));
        outputImagePanelSolutions.addShape(new Circle(compassCenter, compassRadiusOuter, compassRadiusInner, Color.black));
        int markingsCount = 8;
        double markingsStartDist = 40;
        double angleIncrement = (2 * Math.PI) / markingsCount;
        for (int i = 0; i < markingsCount; i++) {
            double angle = angleIncrement * i;
            Vector2 markingVector = new Vector2(Math.sin(angle), Math.sin(Math.cos(angle)));
            Vector2Int markingStart = markingVector.multiply(markingsStartDist).toVector2Int().add(compassCenter);
            Vector2Int markingEnd = markingVector.multiply(compassRadiusOuter + 1).toVector2Int().add(compassCenter);
            outputImagePanelSolutions.addShape(new Line(markingStart, markingEnd, 3, Color.black));
        }
        double pointerLength = 30;
        double compassAngle = 0;
        if (solutions.stream().count() > 0) {
            compassAngle = solutions.get(0).azimuth;
        }
        Vector2Int compassPointerEnd = new Vector2(Math.sin(compassAngle), Math.cos(compassAngle)).multiply(pointerLength).toVector2Int().add(compassCenter);
        outputImagePanelSolutions.addShape(new Line(compassCenter, compassPointerEnd, 2, Color.green));

        outputImagePanelSolutions.redraw();
        //endregion
    }
    private static void resetThings() {
        for (JTextField field : inputFields) {
            field.setText("0");
        }
        doCalculation();
    }
    private static void chargeSpeedTextAction(JTextField textField) {
        verifyTextInputActionListener(textField, false);
        String name = textField.getName();
        int index = Integer.valueOf(name.substring(name.length() - 1)) - 1;
        chargeSpeeds[index] = Double.parseDouble(textField.getText());
    }
    private static void verifyTextInputActionListener(JTextField textField, boolean allowNegativeInput) {
        String text = textField.getText();
        String temp = "";
        boolean foundDecimal = false;
        boolean isNegative = false;

        // Filter out non digits & stuff
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isDigit(c)) {
                temp += c;
            } else if (c == '.' && !foundDecimal) {
                temp += '.';
                foundDecimal = true;
            } else if (c == '-') {
                temp = "";
                isNegative = true;
                foundDecimal = false;
            }
        }

        // Remove leading zeros
        int leadingZerosIndex = 0;
        while (leadingZerosIndex < temp.length() && temp.charAt(leadingZerosIndex) == '0') {
            leadingZerosIndex++;
        }
        temp = temp.substring(leadingZerosIndex);

        // Remove trailing zeros
        int trailingZerosMin = temp.indexOf('.');
        if (trailingZerosMin != -1) {
            int trailingZerosIndex = temp.length();
            while (trailingZerosIndex > trailingZerosMin && temp.charAt(trailingZerosIndex - 1) == '0') {
                trailingZerosIndex--;
            }
            temp = temp.substring(0, trailingZerosIndex);
        }

        // Decimal
        int decimalIndex = temp.indexOf('.');
        if (decimalIndex == 0) {
            temp = '0' + temp;
            decimalIndex++;
        }
        if (decimalIndex == temp.length() - 1) {
            temp += '0';
        }

        // Negative sign
        if (isNegative && allowNegativeInput) {
            temp = '-' + temp;
        }

        textField.setText(temp);
    }
}
