import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import java.awt.Font;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.SwingConstants;
import javax.swing.JSlider;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//Graphical user interface 
public class SimGui {

	private JFrame frame;
	private Controller ctr;
	private JTable table_code;
	private JTable table_gpr;
	private JTable table_spregs;
	private JTable table_sfr;
	private JTable table_stack;
	
	protected DefaultTableModel tbl_CodeView;
	protected DefaultTableModel tbl_Gpr;
	protected DefaultTableModel tbl_SpecialRegs;
	protected DefaultTableModel tbl_SfrRegs;
	protected DefaultTableModel tbl_Stack;
	
	private JComboBox cb_frequence;
	private JPanel panel_Input;
	private JTextField txt_regValue;
	private JComboBox comboBox_H;
	private JComboBox comboBox_V;
	private JRadioButton rdbtn_PinA0, rdbtn_PinA1, rdbtn_PinA2, rdbtn_PinA3, rdbtn_PinA4;
	private JRadioButton rdbtn_PinB0, rdbtn_PinB1, rdbtn_PinB2, rdbtn_PinB3, rdbtn_PinB4, rdbtn_PinB5, rdbtn_PinB6,
			rdbtn_PinB7;
	private JLabel lblTrisA0, lblTrisA1, lblTrisA2, lblTrisA3, lblTrisA4;
	private JLabel lblTrisB0, lblTrisB1, lblTrisB2, lblTrisB3, lblTrisB4, lblTrisB5, lblTrisB6, lblTrisB7;
	private JSlider slider_sleep;
	protected JLabel lblRunTime;
	private JScrollPane scrollPane;


	/**
	 * Launch the application.
	 */
	// GUi Main
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimGui window = new SimGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SimGui() {
		ctr = new Controller(this);
		initialize();
		ctr.initialize();
		ctr.memory.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
//initialize The GUI
	private void initialize() {
		// GUI Frame and Layout
		frame = new JFrame();
		frame.setBounds(100, 100, 1625, 734);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		// general purpose register
		JPanel panel_GPR = new JPanel();
		panel_GPR.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_GPR.setBounds(10, 71, 325, 466);
		frame.getContentPane().add(panel_GPR);
		panel_GPR.setLayout(new BorderLayout(0, 0));

		JScrollPane sp_gpr = new JScrollPane();
		panel_GPR.add(sp_gpr);

		String gprHeader[] = new String[] { "0x", "+0", "+1", "+2", "+3", "+4", "+5", "+6", "+7" };
		// Initialize
		tbl_Gpr = new DefaultTableModel();
		// header
		tbl_Gpr.setColumnIdentifiers(gprHeader);

		table_gpr = new JTable();
		// set model
		table_gpr.setModel(tbl_Gpr);

		sp_gpr.setViewportView(table_gpr);
		// Menu
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 99, 22);
		frame.getContentPane().add(menuBar);
		// Menu bar "File"
		JMenu mnFile = new JMenu("File");
		mnFile.setFont(new Font("Segoe UI", Font.BOLD, 12));
		menuBar.add(mnFile);
		// The button File to choose a file from Windows directory "Load a text file
		// (LST as an example)"
		JButton btnLoadFile = new JButton("Load File");
		btnLoadFile.setFont(new Font("Tahoma", Font.BOLD, 11));
		// FileChooser "Constructs a JFileChooser pointing to the user'sdefault
		// directory"
		btnLoadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Final is used in several contexts to define an entity that can only be
				// assignd one
				final JFileChooser fc = new JFileChooser();
				int fileValue = fc.showOpenDialog(btnLoadFile);
				if (fileValue == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					ctr.clearProgrammMemory();
					ctr.clearCodeTable();
					ctr.clearDebugLine();
					ctr.clearPCLine();
					try {
						ctr.openLstFile(file);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				} else {
					System.out.println("Open command canceled by user");
				}
			}
		});
		mnFile.add(btnLoadFile);

		JButton btnExit = new JButton("Exit!");
		btnExit.setForeground(Color.RED);
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(btnExit);

		JPanel panel_CodeView = new JPanel();
		panel_CodeView.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_CodeView.setBounds(345, 30, 812, 658);
		frame.getContentPane().add(panel_CodeView);
		panel_CodeView.setLayout(new BorderLayout(0, 0));

		JScrollPane sp_code = new JScrollPane();
		sp_code.setPreferredSize(new Dimension(300, 600));
		sp_code.setViewportBorder(UIManager.getBorder("TableHeader.cellBorder"));
		panel_CodeView.add(sp_code);
		// Title
		String codeViewHeader[] = new String[] { " ", "PC", "Code", "Lines", "Label", "Mnemonic" };

		tbl_CodeView = new DefaultTableModel();
		tbl_CodeView.setColumnIdentifiers(codeViewHeader);

		table_code = new JTable();
		table_code.setEnabled(false);
		table_code.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table_code.rowAtPoint(e.getPoint());
				int column = table_code.columnAtPoint(e.getPoint());
				if (column == 0 && (table_code.getValueAt(row, column + 1)).equals("    ") == false) {
					int pc = Integer.valueOf((String) table_code.getValueAt(row, column + 1), 16);
					ctr.setBreakpoint(pc);
					System.out.println("Row " + row + "PC: " + pc);
					if (ctr.debugLine[pc] == false) {
						table_code.setValueAt(" ", row, 0);
					} else {
						table_code.setValueAt("X", row, 0);
					}

				}
			}
		});
		table_code.setModel(tbl_CodeView);
		// columns sizing
		for (int i = 0; i < 6; i++) {
			TableColumn column = table_code.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(30);
				column.setMaxWidth(30);
				column.setMinWidth(30);
				column.setResizable(false);
			} else if (i > 0 && i < 4) {

				column.setPreferredWidth(80);
				column.setMaxWidth(80);
				column.setMinWidth(80);
				column.setResizable(false);
			} else if (i == 4) {

				column.setPreferredWidth(100);
				column.setMaxWidth(100);
				column.setMinWidth(100);
				column.setResizable(false);
			} else {
				column.setResizable(true);
			}
		}
		// Load Data from File
		sp_code.setViewportView(table_code);

		JPanel panel_Control = new JPanel();
		panel_Control.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_Control.setBounds(1167, 32, 434, 58);
		frame.getContentPane().add(panel_Control);
		// Run button
		JButton btn_Run = new JButton("Run!");
		btn_Run.setForeground(Color.GREEN);
		btn_Run.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btn_Run.setBounds(10, 6, 91, 41);
		btn_Run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.startSimu();
			}
		});
		panel_Control.setLayout(null);
		panel_Control.add(btn_Run);
		// Stop button
		JButton btn_Stop = new JButton("Stop!");
		btn_Stop.setForeground(Color.RED);
		btn_Stop.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btn_Stop.setBounds(111, 6, 91, 41);
		btn_Stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.stopctr();
			}
		});
		panel_Control.add(btn_Stop);

		JButton btn_Single_Steps = new JButton("Single Steps");
		btn_Single_Steps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.singleStep();
			}
		});
		btn_Single_Steps.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btn_Single_Steps.setBounds(313, 6, 113, 41);
		panel_Control.add(btn_Single_Steps);

		JButton btn_Reset = new JButton("Reset!");
		btn_Reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.reset();
			}
		});
		btn_Reset.setForeground(Color.ORANGE);
		btn_Reset.setFont(new Font("Tahoma", Font.PLAIN, 13));

		btn_Reset.setBounds(212, 6, 91, 41);
		panel_Control.add(btn_Reset);

		JPanel panel_PortA = new JPanel();
		panel_PortA.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_PortA.setBounds(1167, 332, 434, 97);
		frame.getContentPane().add(panel_PortA);
		panel_PortA.setLayout(null);

		JLabel lbl_PortA = new JLabel("Port A");
		lbl_PortA.setBounds(10, 0, 423, 29);
		lbl_PortA.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_PortA.add(lbl_PortA);

		JLabel lbl_PortA_Tris = new JLabel("Tris");
		lbl_PortA_Tris.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_PortA_Tris.setBounds(10, 28, 46, 13);
		panel_PortA.add(lbl_PortA_Tris);

		JLabel lbl_PortA_Pin = new JLabel("Pin");
		lbl_PortA_Pin.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_PortA_Pin.setBounds(10, 70, 46, 13);
		panel_PortA.add(lbl_PortA_Pin);

		rdbtn_PinA0 = new JRadioButton("0");
		rdbtn_PinA0.setEnabled(false);
		rdbtn_PinA0.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinA0.setBounds(380, 66, 46, 23);
		panel_PortA.add(rdbtn_PinA0);

		rdbtn_PinA1 = new JRadioButton("1");
		rdbtn_PinA1.setEnabled(false);
		rdbtn_PinA1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinA1.setBounds(330, 66, 46, 23);
		panel_PortA.add(rdbtn_PinA1);

		rdbtn_PinA2 = new JRadioButton("2");
		rdbtn_PinA2.setEnabled(false);
		rdbtn_PinA2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinA2.setBounds(280, 66, 46, 23);
		panel_PortA.add(rdbtn_PinA2);

		rdbtn_PinA3 = new JRadioButton("3");
		rdbtn_PinA3.setEnabled(false);
		rdbtn_PinA3.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinA3.setBounds(230, 66, 46, 23);
		panel_PortA.add(rdbtn_PinA3);

		rdbtn_PinA4 = new JRadioButton("4");
		rdbtn_PinA4.setEnabled(false);
		rdbtn_PinA4.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinA4.setBounds(180, 67, 46, 23);
		panel_PortA.add(rdbtn_PinA4);

		lblTrisA0 = new JLabel("");
		lblTrisA0.setEnabled(false);
		lblTrisA0.setBounds(400, 18, 50, 23);
		panel_PortA.add(lblTrisA0);

		lblTrisA1 = new JLabel("");
		lblTrisA1.setEnabled(false);
		lblTrisA1.setBounds(350, 18, 50, 23);
		panel_PortA.add(lblTrisA1);

		lblTrisA2 = new JLabel("");
		lblTrisA2.setEnabled(false);
		lblTrisA2.setBounds(300, 18, 50, 23);
		panel_PortA.add(lblTrisA2);

		lblTrisA3 = new JLabel("");
		lblTrisA3.setEnabled(false);
		lblTrisA3.setBounds(250, 18, 50, 23);
		panel_PortA.add(lblTrisA3);

		lblTrisA4 = new JLabel("");
		lblTrisA4.setEnabled(false);
		lblTrisA4.setBounds(200, 18, 50, 23);
		panel_PortA.add(lblTrisA4);

		JPanel panel_PortB = new JPanel();
		panel_PortB.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_PortB.setBounds(1167, 440, 434, 97);
		frame.getContentPane().add(panel_PortB);
		panel_PortB.setLayout(null);

		JLabel lbl_PortB = new JLabel("Port B");
		lbl_PortB.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lbl_PortB.setBounds(10, 1, 411, 24);
		panel_PortB.add(lbl_PortB);

		JLabel lbl_PortB_Tris = new JLabel("Tris");
		lbl_PortB_Tris.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_PortB_Tris.setBounds(10, 26, 46, 13);
		panel_PortB.add(lbl_PortB_Tris);

		JLabel lbl_PortB_Pin = new JLabel("Pin");
		lbl_PortB_Pin.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl_PortB_Pin.setBounds(11, 73, 46, 13);
		panel_PortB.add(lbl_PortB_Pin);

		rdbtn_PinB0 = new JRadioButton("0");
		rdbtn_PinB0.setEnabled(false);
		rdbtn_PinB0.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinB0.setBounds(380, 70, 46, 23);
		panel_PortB.add(rdbtn_PinB0);

		rdbtn_PinB1 = new JRadioButton("1");
		rdbtn_PinB1.setEnabled(false);
		rdbtn_PinB1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinB1.setBounds(330, 70, 46, 23);
		panel_PortB.add(rdbtn_PinB1);

		rdbtn_PinB2 = new JRadioButton("2");
		rdbtn_PinB2.setEnabled(false);
		rdbtn_PinB2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinB2.setBounds(280, 70, 46, 23);
		panel_PortB.add(rdbtn_PinB2);

		rdbtn_PinB3 = new JRadioButton("3");
		rdbtn_PinB3.setEnabled(false);
		rdbtn_PinB3.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinB3.setBounds(230, 70, 46, 23);
		panel_PortB.add(rdbtn_PinB3);

		rdbtn_PinB4 = new JRadioButton("4");
		rdbtn_PinB4.setEnabled(false);
		rdbtn_PinB4.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinB4.setBounds(180, 70, 46, 23);
		panel_PortB.add(rdbtn_PinB4);

		rdbtn_PinB5 = new JRadioButton("5");
		rdbtn_PinB5.setEnabled(false);
		rdbtn_PinB5.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinB5.setBounds(130, 70, 46, 23);
		panel_PortB.add(rdbtn_PinB5);

		rdbtn_PinB6 = new JRadioButton("6");
		rdbtn_PinB6.setEnabled(false);
		rdbtn_PinB6.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinB6.setBounds(80, 70, 46, 23);
		panel_PortB.add(rdbtn_PinB6);

		rdbtn_PinB7 = new JRadioButton("7");
		rdbtn_PinB7.setEnabled(false);
		rdbtn_PinB7.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtn_PinB7.setBounds(30, 70, 46, 23);
		panel_PortB.add(rdbtn_PinB7);

		lblTrisB0 = new JLabel("");
		lblTrisB0.setEnabled(false);
		lblTrisB0.setBounds(400, 27, 50, 23);
		panel_PortB.add(lblTrisB0);

		lblTrisB1 = new JLabel("");
		lblTrisB1.setEnabled(false);
		lblTrisB1.setBounds(350, 27, 50, 23);
		panel_PortB.add(lblTrisB1);

		lblTrisB2 = new JLabel("");
		lblTrisB2.setEnabled(false);
		lblTrisB2.setBounds(300, 27, 50, 23);
		panel_PortB.add(lblTrisB2);

		lblTrisB3 = new JLabel("");
		lblTrisB3.setEnabled(false);
		lblTrisB3.setBounds(250, 27, 50, 23);
		panel_PortB.add(lblTrisB3);

		lblTrisB4 = new JLabel("");
		lblTrisB4.setEnabled(false);
		lblTrisB4.setBounds(200, 27, 50, 23);
		panel_PortB.add(lblTrisB4);

		lblTrisB5 = new JLabel("");
		lblTrisB5.setEnabled(false);
		lblTrisB5.setBounds(150, 27, 50, 23);
		panel_PortB.add(lblTrisB5);

		lblTrisB6 = new JLabel("");
		lblTrisB6.setEnabled(false);
		lblTrisB6.setBounds(100, 27, 50, 23);
		panel_PortB.add(lblTrisB6);

		lblTrisB7 = new JLabel("");
		lblTrisB7.setEnabled(false);
		lblTrisB7.setBounds(50, 27, 50, 23);
		panel_PortB.add(lblTrisB7);

		JPanel panel_SFR_Bit = new JPanel();
		panel_SFR_Bit.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_SFR_Bit.setBounds(1165, 548, 436, 140);
		frame.getContentPane().add(panel_SFR_Bit);
		panel_SFR_Bit.setLayout(new BorderLayout(0, 0));

		JScrollPane sp_sfr = new JScrollPane();
		panel_SFR_Bit.add(sp_sfr);

		table_sfr = new JTable();
		table_sfr.setFont(new Font("Tahoma", Font.PLAIN, 13));
		sp_sfr.setViewportView(table_sfr);
		tbl_SfrRegs = new DefaultTableModel();
		tbl_SfrRegs.setColumnIdentifiers(
				new Object[] { "Reg", "Bit 7", "Bit 6", "Bit 5", "Bit 4", "Bit 3", "Bit 2", "Bit 2", "Bit 0" });
		table_sfr.setModel(tbl_SfrRegs);

		JLabel lbl_sfrReg = new JLabel("SFR (Bits)");
		lbl_sfrReg.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_SFR_Bit.add(lbl_sfrReg, BorderLayout.NORTH);

		panel_Input = new JPanel();
		panel_Input.setBorder(null);
		panel_Input.setBounds(10, 30, 325, 36);
		frame.getContentPane().add(panel_Input);
		panel_Input.setLayout(null);

		JButton btn_input = new JButton("Input");
		btn_input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.input();
			}
		});
		btn_input.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btn_input.setBounds(5, 7, 89, 27);
		panel_Input.add(btn_input);

		txt_regValue = new JTextField();
		txt_regValue.setBackground(new Color(255, 255, 255));
		txt_regValue.setForeground(Color.BLACK);
		txt_regValue.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txt_regValue.setToolTipText("Specify register value as two-digit hexadecimal");
		txt_regValue.setBounds(104, 11, 87, 21);
		panel_Input.add(txt_regValue);
		txt_regValue.setColumns(10);

		String[] destinyH = { "+0", "+1", "+2", "+3", "+4", "+5", "+6", "+7" };
		comboBox_H = new JComboBox(destinyH);
		comboBox_H.setBounds(267, 10, 48, 22);
		panel_Input.add(comboBox_H);

		String[] destinyV = new String[32];
		for (int i = 0; i < 32; i++) {

			destinyV[i] = (Integer.toHexString(i * 8));
		}
		comboBox_V = new JComboBox(destinyV);
		comboBox_V.setBounds(209, 10, 48, 22);
		panel_Input.add(comboBox_V);

		JPanel panel_specialRegs = new JPanel();
		panel_specialRegs.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_specialRegs.setBounds(10, 548, 325, 140);
		frame.getContentPane().add(panel_specialRegs);
		panel_specialRegs.setLayout(new BorderLayout(0, 0));

		JScrollPane sp_spRegs = new JScrollPane();
		// not to scroll
		panel_specialRegs.add(sp_spRegs, BorderLayout.CENTER);

		JLabel lbl_special_Regs = new JLabel("Special Registers");
		lbl_special_Regs.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel_specialRegs.add(lbl_special_Regs, BorderLayout.NORTH);

		table_spregs = new JTable();
		table_spregs.setFont(new Font("Tahoma", Font.PLAIN, 13));
		// Object spRegs[] =
		tbl_SpecialRegs = new DefaultTableModel();
		tbl_SpecialRegs.setColumnIdentifiers(new Object[] { "SFR", "HEX", "BIN" });
		table_spregs.setModel(tbl_SpecialRegs);
		sp_spRegs.setViewportView(table_spregs);

		JPanel panel_Time = new JPanel();
		panel_Time.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, Color.GRAY));
		panel_Time.setBounds(1167, 130, 331, 140);
		frame.getContentPane().add(panel_Time);
		panel_Time.setLayout(null);

		JTextPane txtpnQuarzfrequenz = new JTextPane();
		txtpnQuarzfrequenz.setBackground(Color.LIGHT_GRAY);
		txtpnQuarzfrequenz.setBounds(10, 96, 133, 28);
		panel_Time.add(txtpnQuarzfrequenz);
		txtpnQuarzfrequenz.setText("Quarz-Frequenz");
		txtpnQuarzfrequenz.setFont(new Font("Tahoma", Font.BOLD, 15));

		String[] iteamsinkhz = { "500", "1000", "2000", "3000", "4000" };
		cb_frequence = new JComboBox(iteamsinkhz);

		cb_frequence.setBounds(205, 96, 78, 28);
		panel_Time.add(cb_frequence);

		JTextPane txtpnRunningTime = new JTextPane();
		txtpnRunningTime.setBackground(Color.LIGHT_GRAY);
		txtpnRunningTime.setText("Running-Time");
		txtpnRunningTime.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtpnRunningTime.setBounds(28, 46, 115, 28);
		panel_Time.add(txtpnRunningTime);

		JLabel lbl_timing = new JLabel("Timing");
		lbl_timing.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		lbl_timing.setBounds(0, 0, 46, 22);
		panel_Time.add(lbl_timing);

		JButton btn_setFrequence = new JButton("Set");
		btn_setFrequence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.updateFrequency(getSelectedFrequency());
			}
		});
		btn_setFrequence.setBounds(147, 96, 56, 28);
		panel_Time.add(btn_setFrequence);

		JTextPane txtpnHz = new JTextPane();
		txtpnHz.setBackground(Color.LIGHT_GRAY);
		txtpnHz.setText("kHz");
		txtpnHz.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtpnHz.setBounds(286, 96, 35, 28);
		panel_Time.add(txtpnHz);

		slider_sleep = new JSlider();
		slider_sleep.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ctr.setSimspeed(slider_sleep.getValue());
				System.out.println(ctr.getSimspeed());
			}
		});
		slider_sleep.setForeground(Color.BLUE);
		slider_sleep.setMinimum(1);
		slider_sleep.setMaximum(10000);
		slider_sleep.setBounds(98, 14, 200, 22);
		panel_Time.add(slider_sleep);
		
		lblRunTime = new JLabel();
		lblRunTime.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblRunTime.setEnabled(true);
		lblRunTime.setBounds(192, 46, 129, 23);
		panel_Time.add(lblRunTime);
		
		JButton btnMCLR = new JButton("MCLR");
		btnMCLR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctr.mclr();
			}
		});
		btnMCLR.setBounds(1167, 281, 72, 23);
		frame.getContentPane().add(btnMCLR);
		
		JPanel panel_Stack = new JPanel();
		panel_Stack.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_Stack.setBounds(1508, 128, 91, 153);
		frame.getContentPane().add(panel_Stack);
		panel_Stack.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_Stack.add(scrollPane, BorderLayout.CENTER);
		table_stack = new JTable();
		tbl_Stack = new DefaultTableModel();
		table_stack.setModel(tbl_Stack);
		tbl_Stack.setColumnIdentifiers(new Object[] {"Stack"});
		scrollPane.setViewportView(table_stack);

	}

	public String getSelectedFrequency() {
		String iteam = (String) this.cb_frequence.getSelectedItem();
		return iteam;
	}

	public int returnChangeAddress() {
		String valueH = (String) this.comboBox_H.getSelectedItem();
		String valueV = (String) this.comboBox_V.getSelectedItem();
		int changeaddress = Integer.valueOf(valueV, 16);
		valueH = valueH.substring(1);
		changeaddress = changeaddress + Integer.valueOf(valueH, 16);
		return changeaddress;
	}

	public JTextField getTxt_regValue() {
		return txt_regValue;
	}

	protected void setTrisA(int trisA) {
		if ((trisA & 0x01) == 1) {
			lblTrisA0.setText("i");
			rdbtn_PinA0.setEnabled(true);
		} else {
			lblTrisA0.setText("O");
			rdbtn_PinA0.setEnabled(false);
		}

		if (((trisA >> 1) & 0x01) == 1) {
			lblTrisA1.setText("i");
			rdbtn_PinA1.setEnabled(true);
		} else {
			lblTrisA1.setText("O");
			rdbtn_PinA1.setEnabled(false);
		}
		if (((trisA >> 2) & 0x01) == 1) {
			lblTrisA2.setText("i");
			rdbtn_PinA2.setEnabled(true);
		} else {
			lblTrisA2.setText("O");
			rdbtn_PinA2.setEnabled(false);
		}
		if (((trisA >> 3) & 0x01) == 1) {
			lblTrisA3.setText("i");
			rdbtn_PinA3.setEnabled(true);
		} else {
			lblTrisA3.setText("O");
			rdbtn_PinA3.setEnabled(false);
		}
		if (((trisA >> 4) & 0x01) == 1) {
			lblTrisA4.setText("i");
			rdbtn_PinA4.setEnabled(true);
		} else {
			lblTrisA4.setText("O");
			rdbtn_PinA4.setEnabled(false);
		}
	}

	protected void setTrisB(int trisB) {
		if ((trisB & 0x01) == 1) {
			lblTrisB0.setText("i");
			rdbtn_PinB0.setEnabled(true);
		} else {
			lblTrisB0.setText("O");
			rdbtn_PinB0.setEnabled(false);
		}
		if (((trisB >> 1) & 0x01) == 1) {
			lblTrisB1.setText("i");
			rdbtn_PinB1.setEnabled(true);
		} else {
			lblTrisB1.setText("O");
			rdbtn_PinB1.setEnabled(false);
		}
		if (((trisB >> 2) & 0x01) == 1) {
			lblTrisB2.setText("i");
			rdbtn_PinB2.setEnabled(true);
		} else {
			lblTrisB2.setText("O");
			rdbtn_PinB2.setEnabled(false);
		}
		if (((trisB >> 3) & 0x01) == 1) {
			lblTrisB3.setText("i");
			rdbtn_PinB3.setEnabled(true);
		} else {
			lblTrisB3.setText("O");
			rdbtn_PinB3.setEnabled(false);
		}
		if (((trisB >> 4) & 0x01) == 1) {
			lblTrisB4.setText("i");
			rdbtn_PinB4.setEnabled(true);
		} else {
			lblTrisB4.setText("O");
			rdbtn_PinB4.setEnabled(false);
		}
		if (((trisB >> 5) & 0x01) == 1) {
			lblTrisB5.setText("i");
			rdbtn_PinB5.setEnabled(true);
		} else {
			lblTrisB5.setText("O");
			rdbtn_PinB5.setEnabled(false);
		}
		if (((trisB >> 6) & 0x01) == 1) {
			lblTrisB6.setText("i");
			rdbtn_PinB6.setEnabled(true);
		} else {
			lblTrisB6.setText("O");
			rdbtn_PinB6.setEnabled(false);
		}
		if (((trisB >> 7) & 0x01) == 1) {
			lblTrisB7.setText("i");
			rdbtn_PinB7.setEnabled(true);
		} else {
			lblTrisB7.setText("O");
			rdbtn_PinB7.setEnabled(false);
		}

	}

	protected int getPortA() {
		int ra = 0x00;
		if (rdbtn_PinA0.isSelected()) {
			ra = ra + 0x01;
		}
		if (rdbtn_PinA1.isSelected()) {
			ra = ra + 0x02;
		}
		if (rdbtn_PinA2.isSelected()) {
			ra = ra + 0x04;
		}
		if (rdbtn_PinA3.isSelected()) {
			ra = ra + 0x08;
		}
		if (rdbtn_PinA4.isSelected()) {
			ra = ra + 0x10;
		}
		return ra;
	}

	protected int getPortB() {
		int rb = 0x00;
		if (rdbtn_PinB0.isSelected()) {
			rb = rb + 0x01;
		}
		if (rdbtn_PinB1.isSelected()) {
			rb = rb + 0x02;
		}
		if (rdbtn_PinB2.isSelected()) {
			rb = rb + 0x04;
		}
		if (rdbtn_PinB3.isSelected()) {
			rb = rb + 0x08;
		}
		if (rdbtn_PinB4.isSelected()) {
			rb = rb + 0x10;
		}
		if (rdbtn_PinB5.isSelected()) {
			rb = rb + 0x20;
		}
		if (rdbtn_PinB6.isSelected()) {
			rb = rb + 0x40;
		}
		if (rdbtn_PinB7.isSelected()) {
			rb = rb + 0x80;
		}
		return rb;
	}

	protected void setPortA(int portA) {
		if ((portA & 0x01) == 1) {
			rdbtn_PinA0.setSelected(true);
		} else {
			rdbtn_PinA0.setSelected(false);
		}
		if (((portA >> 1) & 0x01) == 1) {
			rdbtn_PinA1.setSelected(true);
		} else {
			rdbtn_PinA1.setSelected(false);
		}
		if (((portA >> 2) & 0x01) == 1) {
			rdbtn_PinA2.setSelected(true);
		} else {
			rdbtn_PinA2.setSelected(false);
		}
		if (((portA >> 3) & 0x01) == 1) {
			rdbtn_PinA3.setSelected(true);
		} else {
			rdbtn_PinA3.setSelected(false);
		}
		if (((portA >> 4) & 0x01) == 1) {
			rdbtn_PinA4.setSelected(true);
		} else {
			rdbtn_PinA4.setSelected(false);
		}
	}

	protected void setPortB(int portB) {
		if ((portB & 0x01) == 1) {
			rdbtn_PinB0.setSelected(true);
		} else {
			rdbtn_PinB0.setSelected(false);
		}
		if (((portB >> 1) & 0x01) == 1) {
			rdbtn_PinB1.setSelected(true);
		} else {
			rdbtn_PinB1.setSelected(false);
		}
		if (((portB >> 2) & 0x01) == 1) {
			rdbtn_PinB2.setSelected(true);
		} else {
			rdbtn_PinB2.setSelected(false);
		}
		if (((portB >> 3) & 0x01) == 1) {
			rdbtn_PinB3.setSelected(true);
		} else {
			rdbtn_PinB3.setSelected(false);
		}
		if (((portB >> 4) & 0x01) == 1) {
			rdbtn_PinB4.setSelected(true);
		} else {
			rdbtn_PinB4.setSelected(false);
		}
		if (((portB >> 5) & 0x01) == 1) {
			rdbtn_PinB5.setSelected(true);
		} else {
			rdbtn_PinB5.setSelected(false);
		}
		if (((portB >> 6) & 0x01) == 1) {
			rdbtn_PinB6.setSelected(true);
		} else {
			rdbtn_PinB6.setSelected(false);
		}
		if (((portB >> 7) & 0x01) == 1) {
			rdbtn_PinB7.setSelected(true);
		} else {
			rdbtn_PinB7.setSelected(false);
		}

	}
	
	public void setRowHighlight(int row) {
		this.clearRowHighlight();
		this.table_code.setRowSelectionInterval(row - 1, row - 1);
	}

	private void clearRowHighlight() {
		this.table_code.removeRowSelectionInterval(0, this.table_code.getRowCount() - 1);
	}
}
