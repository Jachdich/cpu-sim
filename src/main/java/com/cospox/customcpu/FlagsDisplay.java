package com.cospox.customcpu;

import java.text.MessageFormat;

import javax.swing.JTextField;

public class FlagsDisplay extends JTextField {
	private static final long serialVersionUID = 1L;

	public FlagsDisplay(String init) {
		super();
	}
	
	@Override
	public void setText(String hex) {
		String newText;
		int num = Integer.parseInt(hex.substring(2), 16);
		String zero   = Integer.toString((num & 0b00001) >> 0);
		String carry  = Integer.toString((num & 0b00010) >> 1);
		String parity = Integer.toString((num & 0b00100) >> 2);
		String less   = Integer.toString((num & 0b01000) >> 3);
		String more   = Integer.toString((num & 0b10000) >> 4);
		
		newText = MessageFormat.format("Z:{0} C:{1} P:{2} L:{3} M:{4}", zero, carry, parity, less, more);
		super.setText(newText);
	}
}

//00000000000000000000000000011111
//zero, carry, parity, less, more