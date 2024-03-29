package com.cospox.customcpu;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class RegisterDisplay extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JComponent[] components = new JComponent[32];
	private Simulator sim;
	
	public RegisterDisplay(LayoutManager l, Simulator sim) {
		super(l);
		this.sim = sim;
		this.components[0] = new JLabel("A:");
		this.components[2] = new JLabel("B:");
		this.components[4] = new JLabel("C:");
		this.components[6] = new JLabel("D:");
		this.components[8] = new JLabel("E:");
		this.components[10] = new JLabel("F:");
		this.components[12] = new JLabel("H:");
		this.components[14] = new JLabel("L:");
		this.components[16] = new JLabel("PC:");
		this.components[18] = new JLabel("SP:");
		this.components[20] = new JLabel("IR:");
		this.components[22] = new JLabel("MAR:");
		this.components[24] = new JLabel("MSR:");
		this.components[26] = new JLabel("MALR:");
		this.components[28] = new JLabel("Z:");
		this.components[30] = new JLabel("T:");
		
		this.components[1] = new JTextField("0x0");
		this.components[3] = new JTextField("0x0");
		this.components[5] = new JTextField("0x0");
		this.components[7] = new JTextField("0x0");
		this.components[9] = new JTextField("0x0");
		this.components[11] = new FlagsDisplay("0x0");
		this.components[13] = new JTextField("0x0");
		this.components[15] = new JTextField("0x0");
		this.components[17] = new JTextField("0x0");
		this.components[19] = new JTextField("0x0");
		this.components[21] = new JTextField("0x0");
		this.components[23] = new JTextField("0x0");
		this.components[25] = new JTextField("0x0");
		this.components[27] = new JTextField("0x0");
		this.components[29] = new JTextField("0x0");
		this.components[31] = new JTextField("0x0");
		//this.components[1] = new JTextField();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		int x = 0, y = 0;
		for (JComponent c: this.components) {
			gbc.gridx = x;
			gbc.gridy = y;
			final int y_f = y;
			if (c.getClass().toString().equals(JTextField.class.toString())) {
				//it's not a label
				gbc.ipadx = 100;
				((JTextComponent) c).getDocument().addDocumentListener(new DocumentListener() {
					  public void changedUpdate(DocumentEvent e) {
					    update();
					  }
					  public void removeUpdate(DocumentEvent e) {
					    update();
					  }
					  public void insertUpdate(DocumentEvent e) {
					    update();
					  }
					  
					  public void update() {
						  textChanged((JTextComponent)c, y_f);
					  }
					});
			} else {
				gbc.ipadx = 0;
			}
			c.setFont(new Font("Courier", Font.PLAIN, 15));
			this.add(c, gbc);
			x += 1;
			if (x > 1) {
				x = 0;
				y += 1;
			}
		}
	}
	
	public void textChanged(JTextComponent c, int index) {
		String text = c.getText();
		//System.out.println(text);
		if (HelperFunctions.isHex(text)) {
			this.updateReg(Long.parseUnsignedLong(text.substring(2), 16), this.getRegFromIndex(index));
		}
	}
	
	private String getRegFromIndex(int index) {
		String a = new String[]{"a", "b", "c", "d", "e", "f", "h", "l", "pc", "sp", "ir", "mar", "msr", "malr", "z", "t"}[index];
		//System.out.println(a);
		return a;
		
	}
	
	public void updateReg(long value, String reg) {
		this.sim.regs.put(reg, value);
	}
	
	public void update(HashMap<String, Long> regs) {
		this.setReg("a", regs.get("a"));
		this.setReg("b", regs.get("b"));
		this.setReg("c", regs.get("c"));
		this.setReg("d", regs.get("d"));
		this.setReg("f", regs.get("f"));
		this.setReg("h", regs.get("h"));
		this.setReg("l", regs.get("l"));
		this.setReg("pc", regs.get("pc"));
		this.setReg("sp", regs.get("sp"));
		this.setReg("ir", regs.get("ir"));
		this.setReg("mar", regs.get("mar"));
		this.setReg("msr", regs.get("msr"));
		this.setReg("malr", regs.get("malr"));
		this.setReg("z", regs.get("z"));
		this.setReg("t", regs.get("t"));
		
	}
	
	public void setReg(String reg, long val) {
		JTextField a = null;
		switch (reg) {
		case "a":
			a = (JTextField)this.components[1]; break;
		case "b":
			a = (JTextField)this.components[3]; break;
		case "c":
			a = (JTextField)this.components[5]; break;
		case "d":
			a = (JTextField)this.components[7]; break;
		case "e":
			a = (JTextField)this.components[9]; break;
		case "f":
			a = (JTextField)this.components[11]; break;
		case "h":
			a = (JTextField)this.components[13]; break;
		case "l":
			a = (JTextField)this.components[15]; break;
		case "pc":
			a = (JTextField)this.components[17]; break;
		case "sp":
			a = (JTextField)this.components[19]; break;
		case "ir":
			a = (JTextField)this.components[21]; break;
		case "mar":
			a = (JTextField)this.components[23]; break;
		case "msr":
			a = (JTextField)this.components[25]; break;
		case "malr":
			a = (JTextField)this.components[27]; break;
		case "z":
			a = (JTextField)this.components[29]; break;
		case "t":
			a = (JTextField)this.components[31]; break;
		default:
			System.out.println("Error: I programmed it wrongly, wrong reg argument to set");
		}
		a.setText("0x" + Long.toHexString(val));
	}
	
	public long getReg(String reg) {
		JTextField a = null;
		switch (reg) {
		case "a":
			a = (JTextField)this.components[1]; break;
		case "b":
			a = (JTextField)this.components[3]; break;
		case "c":
			a = (JTextField)this.components[5]; break;
		case "d":
			a = (JTextField)this.components[7]; break;
		case "e":
			a = (JTextField)this.components[9]; break;
		case "f":
			a = (JTextField)this.components[11]; break;
		case "h":
			a = (JTextField)this.components[13]; break;
		case "l":
			a = (JTextField)this.components[15]; break;
		case "pc":
			a = (JTextField)this.components[17]; break;
		case "sp":
			a = (JTextField)this.components[19]; break;
		case "ir":
			a = (JTextField)this.components[21]; break;
		case "mar":
			a = (JTextField)this.components[23]; break;
		case "msr":
			a = (JTextField)this.components[25]; break;
		case "malr":
			a = (JTextField)this.components[27]; break;
		case "z":
			a = (JTextField)this.components[29]; break;
		case "t":
			a = (JTextField)this.components[31]; break;
		default:
			System.out.println("Error: I programmed it wrongly, wrong reg argument to get");
		}
		return Long.parseLong(a.getText().substring(2), 16);
	}
}
