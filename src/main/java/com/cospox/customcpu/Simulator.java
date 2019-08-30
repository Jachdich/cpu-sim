package com.cospox.customcpu;

import java.util.HashMap;

public class Simulator {
	private static final boolean HIGH = true;
	private static final boolean LOW = false;
	public byte[] mem;
	public long a, b, s, main = 0;
	private boolean clockIsHigh = false;
	private HashMap<String, Long> regs = new HashMap<String, Long>(13);
	
	public Simulator(byte[] mem) {
		this.mem = mem;
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
		this.regs.put("alumode", 0x0L);
		//alumodes
		//0: add
		//1: sub
		//2: mul
		//3: div
		//4: and
		//5: or
		//6: not
		//7: xor
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
	
	private long getALUOut() {
		switch (this.regs.get("alumode").intValue()) {
		case 0:
			return this.a + this.b;
		case 1:
			return this.a - this.b;
		case 2:
			return this.a * this.b;
		case 3:
			return this.a / this.b;
		case 4:
			return this.a & this.b;
		case 5:
			return this.a & this.b;
		case 6:
			return this.a & this.b;
		case 7:
			return this.a & this.b;
		default:
			this.debug("I programmed it wrongly: bad ALU mode");
			return 0L;
		}
	}
	
	private void pullDownResistors() {
		/*
		 * simulate pull-down resistors on the buses:
		 * set all busses to zero before the clock cycle
		 */
		this.s = 0L;
		this.a = 0L;
		this.b = 0L;
		this.main = 0L;
	}
	
	public void clock() {
		if (clockIsHigh) { this.clock_low(); this.clockIsHigh = false; }
		else { this.pullDownResistors(); this.clock_high(); this.clockIsHigh = true; }
	}
	
	public void debug(String text) {
		System.out.println(text);
	}
	
	public void execWord(boolean edge, long word) {
		//Most microinstructions operate on the high edge,
		//such as setting the ALU mode and putting values on the bus.
		//However taking values off of the bus happens on the low edge.
		if (((word >> 0) & 0b1) == 1 && edge)  {  this.debug("msr_on_s");    this.s = this.regs.get("msr"); }
		if (((word >> 1) & 0b1) == 1 && edge)  {  this.debug("alu_or");      this.regs.put("alumode", 5L); }
		if (((word >> 2) & 0b1) == 1 && edge)  {  this.debug("alu_add");     this.regs.put("alumode", 0L); }
		if (((word >> 3) & 0b1) == 1 && !edge) {  this.debug("pc_off_main"); this.regs.put("pc", this.main); }
		if (((word >> 4) & 0b1) == 1 && !edge) {  this.debug("f_off_main");  this.regs.put("f", this.main); }
		if (((word >> 5) & 0b1) == 1 && !edge) {  this.debug("pc_off_s");    this.regs.put("pc", this.s); }
		if (((word >> 6) & 0b1) == 1 && !edge) {  this.debug("mar_off_main");this.regs.put("mar", this.main); }
		if (((word >> 7) & 0b1) == 1 && edge)  {  this.debug("pc_on_a");     this.a = this.regs.get("pc"); }
		if (((word >> 8) & 0b1) == 1 && !edge) {  this.debug("h_off_main");  this.regs.put("h", this.main); }
		if (((word >> 9) & 0b1) == 1 && edge)  {  this.debug("f_on_b");      this.a = this.regs.get("l"); }
		if (((word >> 10) & 0b1) == 1 && edge) {  this.debug("a_on_b");      this.b = this.regs.get("a"); }
		if (((word >> 11) & 0b1) == 1 && edge) {  this.debug("l_on_a");      this.a = this.regs.get("l"); }
		if (((word >> 12) & 0b1) == 1 && !edge) { this.debug("a_off_main");  this.regs.put("a", this.main); }
		if (((word >> 13) & 0b1) == 1 && !edge) { this.debug("d_off_main");  this.regs.put("d", this.main); }
		if (((word >> 14) & 0b1) == 1 && edge)  { this.debug("alu_sub");     this.regs.put("alumode", 1L); }
		if (((word >> 15) & 0b1) == 1 && !edge) { this.debug("z_off_main");  this.regs.put("z", this.main); }
		if (((word >> 16) & 0b1) == 1 && edge)  { this.debug("d_on_b");      this.b = this.regs.get("d"); }
		if (((word >> 17) & 0b1) == 1 && edge)  { this.debug("alu_mul");     this.regs.put("alumode", 2L); }
		if (((word >> 18) & 0b1) == 1 && edge)  { this.debug("d_on_a");      this.a = this.regs.get("d"); }
		if (((word >> 19) & 0b1) == 1 && !edge) { this.debug("l_off_main");  this.regs.put("l", this.main); }
		if (((word >> 20) & 0b1) == 1 && edge)  { this.debug("l_on_b");      this.b = this.regs.get("l"); }
		if (((word >> 21) & 0b1) == 1 && !edge) { this.debug("c_off_main");  this.regs.put("c", this.main); }
		if (((word >> 22) & 0b1) == 1 && edge)  { this.debug("h_on_b");      this.b = this.regs.get("h"); }
		if (((word >> 23) & 0b1) == 1 && edge)  { this.debug("malr_on_b");   this.b = this.regs.get("malr"); }
		if (((word >> 24) & 0b1) == 1 && edge)  { this.debug("h_on_a");      this.a = this.regs.get("h"); }
		if (((word >> 25) & 0b1) == 1 && edge)  { this.debug("a_on_a");      this.a = this.regs.get("a"); }
		if (((word >> 26) & 0b1) == 1 && edge)  { this.debug("alu_xor");     this.regs.put("alumode", 7L); }
		if (((word >> 27) & 0b1) == 1 && !edge) { this.debug("b_off_main");  this.regs.put("b", this.main); }
		if (((word >> 28) & 0b1) == 1 && edge)  { this.debug("c_on_b");      this.b = this.regs.get("c"); }
		if (((word >> 29) & 0b1) == 1 && edge)  { this.debug("c_on_a");      this.a = this.regs.get("c"); }
		if (((word >> 30) & 0b1) == 1 && edge)  { this.debug("f_on_a");      this.a = this.regs.get("f"); }
		if (((word >> 31) & 0b1) == 1 && !edge) { this.debug("ir_off_main"); this.regs.put("ir", this.main); }
		if (((word >> 32) & 0b1) == 1 && edge)  { this.debug("b_on_a");      this.a = this.regs.get("b"); }
		if (((word >> 33) & 0b1) == 1 && edge)  { this.debug("alu_not");     this.regs.put("alumode", 6L); }
		if (((word >> 34) & 0b1) == 1 && edge)  { this.debug("alu_and");     this.regs.put("alumode", 4L); }
		if (((word >> 35) & 0b1) == 1 && !edge) { this.debug("malr_off_s");  this.regs.put("malr", this.s); }
		if (((word >> 36) & 0b1) == 1 && edge)  { this.debug("z_on_s");      this.s = this.regs.get("z"); }
		if (((word >> 37) & 0b1) == 1 && edge)  { this.debug("b_on_b");      this.b = this.regs.get("b"); }
		if (((word >> 38) & 0b1) == 1 && edge)  { this.debug("z_on_main");   this.main = this.regs.get("z"); }
		if (((word >> 39) & 0b1) == 1 && !edge) { this.debug("mar_off_s");   this.regs.put("mar", this.s); }
		if (((word >> 40) & 0b1) == 1 && edge)  { this.debug("alu_div");     this.regs.put("alumode", 3L); }
		if (((word >> 41) & 0b1) == 1 && !edge) { this.debug("msr_off_main");this.regs.put("msr", this.main); }
		if (((word >> 42) & 0b1) == 1 && !edge) { this.debug("reset_t");     this.regs.put("t", -1L); }
		if (((word >> 43) & 0b1) == 1 && edge)  { this.debug("gen_2");       this.b = 2; }
		if (((word >> 44) & 0b1) == 1 && edge)  { this.debug("ram_on_a");    this.a = this.getRam(); }
		if (((word >> 45) & 0b1) == 1 && edge)  { this.debug("e_on_a");      this.a = this.regs.get("e"); }
		if (((word >> 46) & 0b1) == 1 && edge)  { this.debug("e_on_b");      this.b = this.regs.get("e"); }
		if (((word >> 47) & 0b1) == 1 && !edge) { this.debug("e_off_main");  this.regs.put("e", this.main); }
		if (((word >> 48) & 0b1) == 1 && !edge) { this.debug("sp_off_main"); this.regs.put("sp", this.main); }
		if (((word >> 49) & 0b1) == 1 && edge)  { this.debug("sp_on_a");     this.a = this.regs.get("sp"); }
		if (((word >> 50) & 0b1) == 1 && edge)  { this.debug("update_flags");this.updateFlags(); }
		if (((word >> 51) & 0b1) == 1 && !edge) { this.debug("ram_off_main");this.ramOffMain(); }
		if (((word >> 52) & 0b1) == 1 && edge)  { this.debug("gen_1");       this.b = 1; }
		this.main |= this.getALUOut();
		System.out.println();
	}
	
	public void clock_high() {
		if (this.regs.get("t") == 0) {
			this.execWord(HIGH, Microcode.microcode[0]);
		} else if (this.regs.get("t") == 1) {
			this.execWord(HIGH, Microcode.microcode[1]);
		} else {
			this.execWord(HIGH, Microcode.microcode[(int) (Microcode.microcodeIndex[this.regs.get("ir").intValue()] + this.regs.get("t") - 2)]);
		}
	}
	
	public void clock_low() {
		if (this.regs.get("t") == 0) {
			this.execWord(LOW, Microcode.microcode[0]);
		} else if (this.regs.get("t") == 1) {
			this.execWord(LOW, Microcode.microcode[1]);
		} else {
			this.execWord(LOW, Microcode.microcode[(int) (Microcode.microcodeIndex[this.regs.get("ir").intValue()] + this.regs.get("t") - 2)]);
			//this.debug(Microcode.microcodeIndex[(int) (this.regs.get("ir") + this.regs.get("t") - 2)]);
		}
		this.regs.put("t", this.regs.get("t") + 1);
	}
}
