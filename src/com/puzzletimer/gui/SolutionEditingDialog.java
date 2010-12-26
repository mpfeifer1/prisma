package com.puzzletimer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;
import com.puzzletimer.util.SolutionUtils;
import com.puzzletimer.util.StringUtils;

@SuppressWarnings("serial")
public class SolutionEditingDialog extends JDialog {
    public static class SolutionEditingDialogListener {
        public void solutionEdited(Solution solution) {
        }
    }

    private JTextField textFieldStart;
    private JTextField textFieldTime;
    private JComboBox comboBoxPenalty;
    private JTextField textFieldScramble;
    private JButton buttonOk;
    private JButton buttonCancel;

    public SolutionEditingDialog(
            JFrame owner,
            boolean modal,
            final Solution solution,
            final SolutionEditingDialogListener listener) {
        super(owner, modal);

        setTitle("Solution Editor");
        setMinimumSize(new Dimension(480, 200));
        setPreferredSize(getMinimumSize());

        createComponents();

        // start
        DateFormat dateFormat =
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        this.textFieldStart.setText(
            dateFormat.format(solution.getTiming().getStart()));

        // time
        this.textFieldTime.setText(
            SolutionUtils.formatMinutes(solution.getTiming().getElapsedTime()));
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // text selection
                SolutionEditingDialog.this.textFieldTime.setSelectionStart(0);
                SolutionEditingDialog.this.textFieldTime.setSelectionEnd(Integer.MAX_VALUE);

                // focus
                SolutionEditingDialog.this.textFieldTime.requestFocusInWindow();
            }
        });

        // penalty
        this.comboBoxPenalty.addItem("");
        this.comboBoxPenalty.addItem("+2");
        this.comboBoxPenalty.addItem("DNF");
        this.comboBoxPenalty.setSelectedItem(solution.getPenalty());

        // scramble
        this.textFieldScramble.setText(StringUtils.join(" ", solution.getScramble().getSequence()));
        this.textFieldScramble.setCaretPosition(0);

        // ok button
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // timing
                long time =
                    SolutionUtils.parseTime(
                        SolutionEditingDialog.this.textFieldTime.getText());
                Timing timing =
                    new Timing(
                        solution.getTiming().getStart(),
                        new Date(solution.getTiming().getStart().getTime() + time));

                // penalty
                String penalty =
                    (String) SolutionEditingDialog.this.comboBoxPenalty.getSelectedItem();

                listener.solutionEdited(
                    solution
                        .setTiming(timing)
                        .setPenalty(penalty));

                SolutionEditingDialog.this.dispose();
            }
        });
        this.getRootPane().setDefaultButton(this.buttonOk);

        // cancel button
        this.buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                SolutionEditingDialog.this.dispose();
            }
        });

        // esc key closes window
        this.getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    SolutionEditingDialog.this.dispose();
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void createComponents() {
        setLayout(
            new MigLayout(
                "fill, wrap",
                "[pref!][fill]",
                "[pref!]8[pref!]8[pref!]8[pref!]16[bottom]"));

        // labelStart
        add(new JLabel("Start:"));

        // textFieldStart
        this.textFieldStart = new JTextField();
        this.textFieldStart.setEditable(false);
        add(this.textFieldStart);

        // labelTime
        add(new JLabel("Time:"));

        // textFieldTime
        this.textFieldTime = new JTextField();
        add(this.textFieldTime);

        // labelPenalty
        add(new JLabel("Penalty:"));

        // comboBoxPenalty
        this.comboBoxPenalty = new JComboBox();
        add(this.comboBoxPenalty);

        // labelScramble
        add(new JLabel("Scramble:"));

        // textFieldScramble
        this.textFieldScramble = new JTextField();
        this.textFieldScramble.setEditable(false);
        add(this.textFieldScramble);

        // buttonOk
        this.buttonOk = new JButton("OK");
        add(this.buttonOk, "right, width 100, span 2, split");

        // buttonCancel
        this.buttonCancel = new JButton("Cancel");
        add(this.buttonCancel, "width 100");
    }
}