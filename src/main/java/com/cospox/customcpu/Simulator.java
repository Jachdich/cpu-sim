package com.cospox.customcpu;

import java.util.HashMap;

public class Simulator {
	private static final boolean HIGH = true;
	private static final boolean LOW = false;
	public byte[] mem;
	public long a, b, s, main = 0;
	private boolean clockIsHigh = false;
	public HashMap<String, Long> regs = new HashMap<String, Long>(13);
	private HashMap<Integer, String> regs_toname = new HashMap<Integer, String>();
	
	public Simulator(byte[] mem) {
		this.mem = mem;
		
		this.regs_toname.put(0, "a");
		this.regs_toname.put(1, "b");
		this.regs_toname.put(2, "c");
		this.regs_toname.put(3, "d");
		this.regs_toname.put(4, "e");
		this.regs_toname.put(5, "f");
		this.regs_toname.put(6, "h");
		this.regs_toname.put(7, "l");
		this.regs_toname.put(8, "pc");
		this.regs_toname.put(9, "sp");
		this.regs_toname.put(10, "z");
		this.regs_toname.put(11, "ir");
		this.regs_toname.put(12, "mar");
		this.regs_toname.put(13, "mi");
		this.regs_toname.put(14, "bp");
		
		this.regs.put("a", 0x0L);
		this.regs.put("b", 0x0L);
		this.regs.put("c", 0x0L);
		this.regs.put("d", 0x0L);
		this.regs.put("e", 0x0L);
		this.regs.put("f", 0x0L);
		this.regs.put("h", 0x0L);
		this.regs.put("l", 0x0L);
		this.regs.put("pc", -2L);
		this.regs.put("sp", 0x10L);
		this.regs.put("mar", 0x0L);
		this.regs.put("msr", 0x0L);
		this.regs.put("malr", 0x0L);
		this.regs.put("ir", 0x0L);
		this.regs.put("t", 0x0L);
		this.regs.put("z", 0x0L);
		this.regs.put("mi", 0x0L);
		this.regs.put("bp", 0x0L);
	}
	
	public HashMap<String, Long> getRegs() {
		return this.regs;
	}
	
	public HashMap<String, Long> getBuses() {
		HashMap<String, Long> h = new HashMap<String, Long>();
		h.put("a", this.a);
		h.put("b", this.b);
		h.put("s", this.s);
		h.put("main", this.main);
		return h;
	}
	
	private int getRam() {
		/*
		 * get two bytes of RAM, at address pointed to by MAR
		 */
		long mar = this.regs.get("mar");
		int a = ((this.mem[(int) mar] << 8) | this.mem[(int) mar + 1]) & 0xffff;
		this.debug("Mem read: " + Integer.toHexString(a));
		return a;
	}
	
	private void ramOffMain() {
		long mar = this.regs.get("mar");
		int data = (int) this.main;
		this.mem[(int) mar] = (byte) ((data >> 8) & 0xff);
		this.mem[(int) mar + 1] = (byte) (data & 0xff);
	}
	
	private void updateFlags() {
		if (this.main == 0) {
			this.regs.put("f", this.regs.get("f") | 0x10000000); //TODO
		}
	}
	
	private long getALUOut(int op) {
		System.out.println("op is " + (this.a + this.b));
		switch (op) {
		case 0:
			return this.a + this.b;
		case 1:
			return this.a - this.b;
		case 2:
			return this.a * this.b;
		case 3:
			return this.a / this.b;
		case 4:
			return this.a & this.b; //TODO change!!
		case 5:
			return this.a & this.b;
		case 6:
			return this.a & this.b;
		case 7:
			return this.a & this.b;
		case 0xF:
			return 0L;
		default:
			this.debug("I programmed it wrongly: bad ALU mode");
			return 0L;
		}
	}
	
	private void pullDownResistors() {
		/*
		 * simulate pull-down resistors on the buses:
		 * set all buses to zero before the clock cycle
		 */
		this.s = 0L;
		this.a = 0L;
		this.b = 0L;
		this.main = 0L;
	}
	
	public void clock() {
		if (clockIsHigh) { this.clock(LOW); this.clockIsHigh = false; }
		else { this.pullDownResistors(); this.clock(HIGH); this.clockIsHigh = true; }
	}
	
	public void debug(String text) {
		System.out.println(text);
	}
	
	public void execWord(boolean edge, long word) {
		//Most microinstructions operate on the high edge,
		//such as setting the ALU mode and putting values on the bus.
		//However taking values off of the bus happens on the low edge.
		
		//System.out.println(Long.toBinaryString(word));
		
		if (edge == HIGH) {
			int rega = (int)((word >> 21) & 0xF);
			int regb = (int)((word >> 17) & 0xF);
			int aluop = (int)((word >> 9) & 0b111);
			
			int ram = (int)((word >> 1) & 0b11);
			int gen = (int)((word >> 3) & 0b11);
			
			if (((gen >> 1) & 0b1) == 1) {
				this.b = 2;
			}
			if ((gen & 0b1) == 1) {
				this.b = 1;
			}
			
			if ((ram & 0b1) == 1) {
				this.a = this.getRam();
			}
			
			
			if (rega != 0b1111) {
				this.a = this.regs.get(this.regs_toname.get(rega));
			}
			if (regb != 0b1111) {
				this.b = this.regs.get(this.regs_toname.get(regb));
			}
			this.main |= this.getALUOut(aluop);
			
			//add more later idk
		} else {
			int regmain = (int)((word >> 13) & 0xF);
			if (regmain != 0xF) {
				this.regs.put(this.regs_toname.get(regmain), this.main);
			}
			
			if (((word >> 12) & 0b1) == 1) {
				this.regs.put("mar", this.main);
			}
			
			if (((word >> 5) & 0x1) == 1) {
				this.regs.put("t", -1L);
			}
		}
		
		
//		if (((word >> 0) & 0b1) == 1 && edge)  {  this.debug("msr_on_s");    this.s = this.regs.get("msr"); }
//		if (((word >> 1) & 0b1) == 1 && edge)  {  this.debug("alu_or");      this.regs.put("alumode", 5L); }
//		if (((word >> 2) & 0b1) == 1 && edge)  {  this.debug("alu_add");     this.regs.put("alumode", 0L); }
//		if (((word >> 3) & 0b1) == 1 && !edge) {  this.debug("pc_off_main"); this.regs.put("pc", this.main); }
//		if (((word >> 4) & 0b1) == 1 && !edge) {  this.debug("f_off_main");  this.regs.put("f", this.main); }
//		if (((word >> 5) & 0b1) == 1 && !edge) {  this.debug("pc_off_s");    this.regs.put("pc", this.s); }
//		if (((word >> 6) & 0b1) == 1 && !edge) {  this.debug("mar_off_main");this.regs.put("mar", this.main); }
//		if (((word >> 7) & 0b1) == 1 && edge)  {  this.debug("pc_on_a");     this.a = this.regs.get("pc"); }
//		if (((word >> 8) & 0b1) == 1 && !edge) {  this.debug("h_off_main");  this.regs.put("h", this.main); }
//		if (((word >> 9) & 0b1) == 1 && edge)  {  this.debug("f_on_b");      this.a = this.regs.get("l"); }
//		if (((word >> 10) & 0b1) == 1 && edge) {  this.debug("a_on_b");      this.b = this.regs.get("a"); }
//		if (((word >> 11) & 0b1) == 1 && edge) {  this.debug("l_on_a");      this.a = this.regs.get("l"); }
//		if (((word >> 12) & 0b1) == 1 && !edge) { this.debug("a_off_main");  this.regs.put("a", this.main); }
//		if (((word >> 13) & 0b1) == 1 && !edge) { this.debug("d_off_main");  this.regs.put("d", this.main); }
//		if (((word >> 14) & 0b1) == 1 && edge)  { this.debug("alu_sub");     this.regs.put("alumode", 1L); }
//		if (((word >> 15) & 0b1) == 1 && !edge) { this.debug("z_off_main");  this.regs.put("z", this.main); }
//		if (((word >> 16) & 0b1) == 1 && edge)  { this.debug("d_on_b");      this.b = this.regs.get("d"); }
//		if (((word >> 17) & 0b1) == 1 && edge)  { this.debug("alu_mul");     this.regs.put("alumode", 2L); }
//		if (((word >> 18) & 0b1) == 1 && edge)  { this.debug("d_on_a");      this.a = this.regs.get("d"); }
//		if (((word >> 19) & 0b1) == 1 && !edge) { this.debug("l_off_main");  this.regs.put("l", this.main); }
//		if (((word >> 20) & 0b1) == 1 && edge)  { this.debug("l_on_b");      this.b = this.regs.get("l"); }
//		if (((word >> 21) & 0b1) == 1 && !edge) { this.debug("c_off_main");  this.regs.put("c", this.main); }
//		if (((word >> 22) & 0b1) == 1 && edge)  { this.debug("h_on_b");      this.b = this.regs.get("h"); }
//		if (((word >> 23) & 0b1) == 1 && edge)  { this.debug("malr_on_b");   this.b = this.regs.get("malr"); }
//		if (((word >> 24) & 0b1) == 1 && edge)  { this.debug("h_on_a");      this.a = this.regs.get("h"); }
//		if (((word >> 25) & 0b1) == 1 && edge)  { this.debug("a_on_a");      this.a = this.regs.get("a"); }
//		if (((word >> 26) & 0b1) == 1 && edge)  { this.debug("alu_xor");     this.regs.put("alumode", 7L); }
//		if (((word >> 27) & 0b1) == 1 && !edge) { this.debug("b_off_main");  this.regs.put("b", this.main); }
//		if (((word >> 28) & 0b1) == 1 && edge)  { this.debug("c_on_b");      this.b = this.regs.get("c"); }
//		if (((word >> 29) & 0b1) == 1 && edge)  { this.debug("c_on_a");      this.a = this.regs.get("c"); }
//		if (((word >> 30) & 0b1) == 1 && edge)  { this.debug("f_on_a");      this.a = this.regs.get("f"); }
//		if (((word >> 31) & 0b1) == 1 && !edge) { this.debug("ir_off_main"); this.regs.put("ir", this.main); }
//		if (((word >> 32) & 0b1) == 1 && edge)  { this.debug("b_on_a");      this.a = this.regs.get("b"); }
//		if (((word >> 33) & 0b1) == 1 && edge)  { this.debug("alu_not");     this.regs.put("alumode", 6L); }
//		if (((word >> 34) & 0b1) == 1 && edge)  { this.debug("alu_and");     this.regs.put("alumode", 4L); }
//		if (((word >> 35) & 0b1) == 1 && !edge) { this.debug("malr_off_s");  this.regs.put("malr", this.s); }
//		if (((word >> 36) & 0b1) == 1 && edge)  { this.debug("z_on_s");      this.s = this.regs.get("z"); }
//		if (((word >> 37) & 0b1) == 1 && edge)  { this.debug("b_on_b");      this.b = this.regs.get("b"); }
//		if (((word >> 38) & 0b1) == 1 && edge)  { this.debug("z_on_main");   this.main = this.regs.get("z"); }
//		if (((word >> 39) & 0b1) == 1 && !edge) { this.debug("mar_off_s");   this.regs.put("mar", this.s); }
//		if (((word >> 40) & 0b1) == 1 && edge)  { this.debug("alu_div");     this.regs.put("alumode", 3L); }
//		if (((word >> 41) & 0b1) == 1 && !edge) { this.debug("msr_off_main");this.regs.put("msr", this.main); }
//		if (((word >> 42) & 0b1) == 1 && !edge) { this.debug("reset_t");     this.regs.put("t", -1L); }
//		if (((word >> 43) & 0b1) == 1 && edge)  { this.debug("gen_2");       this.b = 2; }
//		if (((word >> 44) & 0b1) == 1 && edge)  { this.debug("ram_on_a");    this.a = this.getRam(); }
//		if (((word >> 45) & 0b1) == 1 && edge)  { this.debug("e_on_a");      this.a = this.regs.get("e"); }
//		if (((word >> 46) & 0b1) == 1 && edge)  { this.debug("e_on_b");      this.b = this.regs.get("e"); }
//		if (((word >> 47) & 0b1) == 1 && !edge) { this.debug("e_off_main");  this.regs.put("e", this.main); }
//		if (((word >> 48) & 0b1) == 1 && !edge) { this.debug("sp_off_main"); this.regs.put("sp", this.main); }
//		if (((word >> 49) & 0b1) == 1 && edge)  { this.debug("sp_on_a");     this.a = this.regs.get("sp"); }
//		if (((word >> 50) & 0b1) == 1 && edge)  { this.debug("update_flags");this.updateFlags(); }
//		if (((word >> 51) & 0b1) == 1 && !edge) { this.debug("ram_off_main");this.ramOffMain(); }
//		if (((word >> 52) & 0b1) == 1 && edge)  { this.debug("gen_1");       this.b = 1; }
//		this.main |= this.getALUOut();
//		System.out.println();
	}
	
//	public void clock_high() {
//		if (this.regs.get("t") == 0) {
//			this.execWord(HIGH, Microcode.microcode[0]);
//		} else if (this.regs.get("t") == 1) {
//			this.execWord(HIGH, Microcode.microcode[1]);
//		} else {
//			this.execWord(HIGH, Microcode.microcode[(int) (Microcode.microcodeIndex[this.regs.get("ir").intValue()] + this.regs.get("t") - 2)]);
//		}
//	}
	
	public void clock(boolean edge) {
		long mcword = 0;
		if (this.regs.get("t") == 0) {
			mcword |= 0b1000 << 21; //pc on a
			mcword |= 0b1111 << 17; //no reg on b
			mcword |= 0b10   << 3;  //2 on b
			mcword |= 0b000  << 9;  //add
			mcword |= 0b1000 << 13; //pc off main
			mcword |= 0b1    << 12; //mar off main
		} else if (this.regs.get("t") == 1) {
			mcword |= 0b1111 << 21; //no reg on a
			mcword |= 0b1111 << 17; //no reg on b
			mcword |= 0b01  << 1;  //RAM on a
			mcword |= 0b000 << 9;  //add
			mcword |= 0b1011 << 13;//ir off main
		} else {
			//this.execWord(LOW, Microcode.microcode[(int) (Microcode.microcodeIndex[this.regs.get("ir").intValue()] + this.regs.get("t") - 2)]);
			if (this.regs.get("t").intValue() == 2) {
				if (((this.regs.get("ir") >> 8) & 0xf) == 0b1100) {
					//one operand

					/*
					00000000 add  reg,reg
					00000001 sub  reg,reg
					00000010 mul  reg,reg
					00000011 div  reg,reg
					00000100 and  reg,reg
					00000101 or   reg,reg
					00000110 xor  reg,reg
					00000111 cmp  reg,reg
					00001000 mov  reg,reg
					*/
				} else {
					//two or zero operands
					
					int rega = (int)((this.regs.get("ir") >> 4) & 0xF);
					int regb = (int)((this.regs.get("ir") >> 0) & 0xF);
					
					//mov
					System.out.println(Long.toBinaryString(this.regs.get("ir") >> 8));
					if (this.regs.get("ir") >> 8 == 0b00001000) {
						if (rega == regb) {
							mcword |= 0b1000 << 21; //pc on a
							mcword |= 0b1111 << 17; //no reg on b
							mcword |= 0b10   << 3;  //2 on b
							mcword |= 0b000  << 9;  //add
							mcword |= 0b1000 << 13; //pc off main
							mcword |= 0b1    << 12; //mar off main
						} else {
							mcword |= 1 << 5;
							mcword |= rega << 21;
							mcword |= 0b1111 << 17; //no reg on b
							mcword |= regb << 13;
							mcword |= 1 << 5;
						}
						
					//cmp
					} else if (this.regs.get("ir") >> 8 == 0b00000111) {
						mcword |= rega << 21;
						mcword |= regb << 17;
						mcword |= 0b001 << 9;
						mcword |= regb << 13;
						mcword |= 1;
						mcword |= 1 << 5;
					} else {
						int oper = (int)((this.regs.get("ir") >> 8) & 0xFF);
						mcword |= rega << 21;
						mcword |= regb << 17;
						mcword |= (oper & 0b111) << 9;
						mcword |= regb << 13;
						mcword |= 1;
						mcword |= 1 << 5;
					}
				}
			}
			
			if (this.regs.get("t").intValue() == 3) {
				if (((this.regs.get("ir") >> 8) & 0xf) == 0b1100) {
					//one operand

				} else {
					//two or zero operands
					
					int rega = (int)((this.regs.get("ir") >> 4) & 0xF);
					int regb = (int)((this.regs.get("ir") >> 0) & 0xF);
					
					//mov
					System.out.println(Long.toBinaryString(this.regs.get("ir") >> 8));
					if (this.regs.get("ir") >> 8 == 0b00000101) {
						if (rega == regb) {
							mcword |= 0b1111 << 21; //no reg on a
							mcword |= 0b1111 << 17; //no reg on b
							mcword |= 0b01  << 1;   //RAM on a
							mcword |= 0b000 << 9;   //add
							mcword |= rega << 13;   //reg a off main
							mcword |= 1 << 5;
						}
					} else {
					}
				}
			}
			//this.debug(Microcode.microcodeIndex[(int) (this.regs.get("ir") + this.regs.get("t") - 2)]);
		}
		this.execWord(edge, mcword);
		if (!edge) {
			this.regs.put("t", this.regs.get("t") + 1);
		}
	}
}
