import tkinter, threading, time
from PIL import Image, ImageTk

class Processor:
    def __init__(self, wordsize=16, ramsize=64*1024, update_function=None, sleep_delay=0):
        """
        FLAGS register f:
        
        BIT    FUNCTION
        0      carry
        1      zero
        2      parity
        3      pos
        4      neg
        """
        self.sleep_delay = sleep_delay
        self.update_function = update_function
        self.WORDSIZE = wordsize
        self.RAM = [0 for i in range(ramsize)]
        self.ramsize = ramsize
        self.stackpointer = 64
        self.a = 0
        self.b = 0
        self.c = 0
        self.d = 0
        self.e = 0
        self.f = ["0"] * 8
        self.h = 0
        self.l = 0
        self.z = 0
        self.OUT = 0
        self.PC = -1
        self.MAR = 0
        self.MSR = 0
        self.MALR = 0
        self.IR = 0
        self.SP = 0
        self.bus_s = 0
        self.bus_a = 0
        self.bus_b = 0
        self.bus_main = 0
        self.ports = [0, 0, 0, 0, 0, 0, 0, 0]
        self.halt = False
        self.T = 0
        self.MICROCODE = {"fd":[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
[self.z_on_s, self.PC_off_s, self.RAM_on_main, self.IR_off_main], "fd"],

0:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main],
   [self.z_on_s, self.PC_off_s, self.MAR_off_s], [self.reset_T], "nop"],

 1:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
[self.z_on_s, self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.a_off_main, self.reset_T], "ld a, imm"],

 2:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
[self.z_on_s, self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.b_off_main, self.reset_T], "ld b, imm"],

 3:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
    [self.z_on_s, self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.c_off_main, self.reset_T], "ld c, imm"],

 4:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
    [self.z_on_s, self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.d_off_main, self.reset_T], "ld d, imm"],

 5:[[self.PC_on_b, self.gen_1,    self.ALU_add,   self.z_off_main,  self.MAR_off_main], 
    [self.z_on_s,  self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.f_off_main, self.reset_T], "ld f, imm"],

 6:[[self.PC_on_b, self.gen_1, self.ALU_add, self.MAR_off_main],
    [self.RAM_on_main, self.h_off_main],
    [self.PC_on_b, self.gen_2, self.ALU_add, self.MAR_off_main, self.z_off_main],
    [self.RAM_on_main, self.l_off_main, self.z_on_s, self.PC_off_s, self.reset_T], "ld hl, imm"],

 7:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
    [self.z_on_s, self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.MSR_off_main], 
    [self.MSR_on_s, self.MAR_off_s, self.RAM_on_main, self.a_off_main, self.reset_T], "ld a, abs"],

 8:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
    [self.z_on_s, self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.MSR_off_main], 
    [self.MSR_on_s, self.MAR_off_s, self.RAM_on_main, self.b_off_main, self.reset_T], "ld b, abs"],

 9:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
    [self.z_on_s, self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.MSR_off_main], 
    [self.MSR_on_s, self.MAR_off_s, self.RAM_on_main, self.c_off_main, self.reset_T], "ld c, abs"],

 10:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
    [self.z_on_s, self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.MSR_off_main], 
    [self.MSR_on_s, self.MAR_off_s, self.RAM_on_main, self.d_off_main, self.reset_T], "ld d, abs"],

 11:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main], 
    [self.z_on_s, self.PC_off_s, self.MAR_off_s, self.RAM_on_main, self.MSR_off_main], 
    [self.MSR_on_s, self.MAR_off_s, self.RAM_on_main, self.f_off_main, self.reset_T], "ld f, abs"],

 12:[[self.PC_on_b, self.gen_1, self.ALU_add, self.z_off_main, self.MAR_off_main],
     [self.RAM_on_main, self.MSR_off_main, self.z_on_s, self.PC_off_s],
     [self.MSR_on_s, self.MALR_off_s, self.MAR_off_s],
     [self.MALR_on_b, self.gen_1, self.ALU_add, self.z_off_main],
     [self.RAM_on_main, self.h_off_main, self.MSR_on_s, self.MAR_off_s],
     [self.RAM_on_main, self.l_off_main, self.reset_T], "ld hl, abs"],

 13:[[self.a_on_a, self.a_on_b, self.ALU_add, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "add a, a"],

 14:[[self.a_on_a, self.b_on_b, self.ALU_add, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "add a, b"],

 15:[[self.a_on_a, self.c_on_b, self.ALU_add, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "add a, c"],

 16:[[self.a_on_a, self.d_on_b, self.ALU_add, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "add a, d"],

 18:[[self.c_on_a, self.l_on_b, self.ALU_adc, self.z_off_main],
     [self.z_on_main, self.c_off_main],
     [self.a_on_a, self.h_on_b, self.ALU_add, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "add hl, ac"],

 19:[[self.a_on_a, self.a_on_b, self.ALU_sub, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "sub a, a"],

 20:[[self.a_on_a, self.b_on_b, self.ALU_sub, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "sub a, b"],

 21:[[self.a_on_a, self.c_on_b, self.ALU_sub, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "sub a, c"],

 22:[[self.a_on_a, self.d_on_b, self.ALU_sub, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "sub a, d"],
 
 25:[[self.a_on_a, self.a_on_b, self.ALU_div, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "div a, a"],

 26:[[self.a_on_a, self.b_on_b, self.ALU_div, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "div a, b"],

 27:[[self.a_on_a, self.c_on_b, self.ALU_div, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "div a, c"],

 28:[[self.a_on_a, self.d_on_b, self.ALU_div, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "div a, d"],

 30:[[self.a_on_a, self.a_on_b, self.ALU_mul, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "mul a, a"],

 31:[[self.a_on_a, self.b_on_b, self.ALU_mul, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "mul a, b"],

 32:[[self.a_on_a, self.c_on_b, self.ALU_mul, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "mul a, c"],

 33:[[self.a_on_a, self.d_on_b, self.ALU_mul, self.z_off_main],
     [self.z_on_main, self.a_off_main, self.reset_T], "mul a, d"],
 
254:[[self.output, self.reset_T], "output"],
255:[[self.stop_the_clock, self.reset_T], "hlt"],
                         }

    def _reset(self):
        self.RAM = [0 for i in range(self.ramsize)]
        self.stackpointer = 64
        self.a = 0
        self.b = 0
        self.c = 0
        self.d = 0
        self.e = 0
        self.f = ["0"] * 8
        self.h = 0
        self.l = 0
        self.z = 0
        self.OUT = 0
        self.PC = -1
        self.MAR = 0
        self.MSR = 0
        self.MALR = 0
        self.IR = 0
        self.SP = 0
        self.bus_s = 0
        self.bus_a = 0
        self.bus_b = 0
        self.bus_main = 0
        self.ports = [0, 0, 0, 0, 0, 0, 0, 0]
        self.halt = False
        self.T = 0
        self.update_function()
        
    def output(self):
        self.OUT = self.a
        
    def stop_the_clock(self):
        self.halt = True

    def h_off_main(self):
        self.h = self.bus_main

    def gen_2(self):
        self.bus_a = 2

    def l_off_main(self):
        self.l = self.bus_main

    def MALR_off_s(self):
        self.MALR = self.bus_s

    def MALR_on_b(self):
        self.bus_b = self.MALR

    def c_on_a(self):
        self.bus_a = self.c

    def l_on_b(self):
        self.bus_b = self.l

    def h_on_b(self):
        self.bus_b = self.h

    def b_on_b(self):
        self.bus_b = self.b

    def c_on_b(self):
        self.bus_b = self.c

    def d_on_b(self):
        self.bus_b = self.d

    def a_on_a(self):
        self.bus_a = self.a

    def a_on_b(self):
        self.bus_b = self.a

    def z_on_main(self):
        self.bus_main = self.z
   
    def ALU_add(self):
        self.bus_main = self.bus_a + self.bus_b
        if self.bus_main > 2**self.WORDSIZE:
            self.bus_main = self.bus_main - 2**self.WORDSIZE
            self.f[0] = "1"
        else:
            self.f[0] = "0"

        if self.bus_main == 0:
            self.f[1] = "1"
        else:
            self.f[1] = "0"

    def ALU_mul(self):
        self.bus_main = self.bus_a * self.bus_b
        if self.bus_main > 2**self.WORDSIZE:
            self.bus_main = self.bus_main - 2**self.WORDSIZE
            self.f[0] = "1"
        else:
            self.f[0] = "0"

        if self.bus_main == 0:
            self.f[1] = "1"
        else:
            self.f[1] = "0"

    def ALU_div(self):
        self.bus_main = self.bus_a / self.bus_b
        if self.bus_main > 2**self.WORDSIZE:
            self.bus_main = self.bus_main - 2**self.WORDSIZE
            self.f[0] = "1"
        else:
            self.f[0] = "0"

        if self.bus_main == 0:
            self.f[1] = "1"
        else:
            self.f[1] = "0"

    def ALU_adc(self):
        self.bus_main = self.bus_a + self.bus_b + int(f[0])
        if self.bus_main > 2**self.WORDSIZE:
            self.bus_main = self.bus_main - 2**self.WORDSIZE
            self.f[0] = "1"
        else:
            self.f[0] = "0"

        if self.bus_main == 0:
            self.f[1] = "1"
        else:
            self.f[1] = "0"

    def ALU_sub(self):
        self.bus_main = self.bus_a - self.bus_b
        if self.bus_main < 0-2**self.WORDSIZE:
            self.bus_main = self.bus_main + 2**self.WORDSIZE
            self.f[0] = "1"
        else:
            self.f[0] = "0"

        if self.bus_main == 0:
            self.f[1] = "1"
        else:
            self.f[1] = "0"

    def ALU_sbc(self):
        self.bus_main = self.bus_a - self.bus_b - int(f[0])
        if self.bus_main < 0-2**self.WORDSIZE:
            self.bus_main = self.bus_main + 2**self.WORDSIZE
            self.f[0] = "1"
        else:
            self.f[0] = "0"

        if self.bus_main == 0:
            self.f[1] = "1"
        else:
            self.f[1] = "0"
        
    def reset_T(self):
        self.T = 0
        
    def PC_on_b(self):
        self.bus_b = self.PC

    def gen_1(self):
        self.bus_a = 1

    def z_off_main(self):
        self.z = self.bus_main

    def MAR_off_s(self):
        self.MAR = self.bus_s

    def MSR_off_main(self):
        self.MSR = self.bus_main

    def MSR_on_s(self):
        self.bus_s = self.MSR

    def MALR_on_main(self):
        self.bus_main = self.MALR
        
    def MAR_off_main(self):
        self.MAR = self.bus_main

    def z_on_s(self):
        self.bus_s = self.z

    def PC_off_s(self):
        self.PC = self.bus_s

    def RAM_on_main(self):
        self.bus_main = self.RAM[self.MAR]

    def IR_off_main(self):
        self.IR = self.bus_main

    def a_off_main(self):
        self.a = self.bus_main

    def b_off_main(self):
        self.b = self.bus_main
        
    def c_off_main(self):
        self.c = self.bus_main

    def d_off_main(self):
        self.d = self.bus_main

    def f_off_main(self):
        self.f = [i for i in bin(self.bus_main)[2:]]

    def write_port(self, port):
        self.ports[port] = self.bus_main

    def read_port(self, port):
        self.bus_main = self.ports[port]
        
    def clock_cycle(self):
        if self.T < 2:
            for microcode in self.MICROCODE["fd"][self.T]:
                microcode()
            self.T += 1
        else:
            if self.T == 2:
                t0 = time.time()
                self.debug(self.MICROCODE[self.IR][-1])
                t1 = time.time()
                #print("debug: ", (t1-t0) * 100)
            t0 = time.time()
            print(self.IR)
            for microcode in self.MICROCODE[self.IR][self.T - 2]:
                microcode()
            if not self.T == 0:
                self.T += 1
            t1 = time.time()
            #print("microcode", (t1-t0) * 100)
        
        if self.update_function:
            t0 = time.time()
            self.update_function()
            t1 = time.time()
            #print("update:", (t1-t0) * 100)
        
    def __exec__(self):
        while not self.halt:
            self.clock_cycle()
            time.sleep(self.sleep_delay)
            
    def _update_display(self):
        pass

class Display(Processor):
    def __init__(self, wordsize=8, ramsize=128):
        super().__init__(wordsize, ramsize, update_function=self.update)
        self.root = tkinter.Tk()
        
        self.regs_text = tkinter.StringVar(self.root, value=self.get_register_contents())
        self.regs = tkinter.Label(self.root, textvar=self.regs_text)
        self.regs.grid(row=0, column=2, rowspan=100)

        self.filled_image   = ImageTk.PhotoImage(Image.open("filled.png"))
        self.unfilled_image = ImageTk.PhotoImage(Image.open("unfilled.png"))
        
        self.led_image = tkinter.Label(self.root, image=self.unfilled_image)
        self.led_image.grid(row=100, column=2, rowspan=100)

        self.mem_text = tkinter.StringVar(self.root)
        self.mem_box  = tkinter.Text(self.root)
        self.mem_box.grid(row=0, column=1, rowspan=100)
        self.mem_box.insert("1.0", [str(i) for i in self.RAM])

        self.run_button = tkinter.Button(self.root, text ="Run ", command=self.exec_code)
        self.run_button.grid(row=0, column=0)

        self.stop_button = tkinter.Button(self.root, text ="Stop", command=self.stop_thread)
        self.stop_button.grid(row=1, column=0)

        self.step_button = tkinter.Button(self.root, text="Step", command=self.single_step)
        self.step_button.grid(row=2, column=0)

        self.freq_label = tkinter.Label(self.root, text="Freq:")
        self.freq_label.grid(row=3, column=0)
    
        self.freq_box = tkinter.Entry(self.root, width=4)
        self.freq_box.grid(row=4, column=0)

        self.fast_mode_selector = tkinter.Checkbutton(self.root, text="Fast Mode")
        self.fast_mode_selector.grid(row=5, column=0)

        self.seperator = tkinter.Label(self.root, text=" ")
        self.seperator.grid(row=6, column=0)

        self.reset_button = tkinter.Button(self.root, text="Reset", command=self._reset)
        self.reset_button.grid(row=7, column=0)

        self.quit_button = tkinter.Button(self.root, text="Quit", command=self.root.destroy)
        self.quit_button.grid(row=8, column=0)

        self.log_box = tkinter.Text(self.root, width=10)
        self.log_box.grid(row=0, column=3, rowspan=100)
        self.log_box.config(state="disabled")

        self.menu = tkinter.Menu(self.root)

        self.filemenu = tkinter.Menu(self.menu, tearoff=0)
        self.filemenu.add_command(label="Save", command=self.save_file, accelerator="Ctrl-s")
        self.filemenu.add_command(label="Save as...", command=self.saveas_window, accelerator="Ctrl-S")
        self.filemenu.add_command(label="Open...", command=self.open_window, accelerator="Ctrl-o")
        self.filemenu.add_separator()
        self.filemenu.add_command(label="Quit", command=self.save_and_close, accelerator="Ctrl-q")
        self.filemenu.add_command(label="Quit without saving", command=self.root.destroy, accelerator="Ctrl-Q")

        self.menu.add_cascade(label="File", menu=self.filemenu)
        self.root.config(menu=self.menu)
        
        self.root.bind("<Control-s>", self.save_file)
        self.root.bind("<Control-o>", self.open_window)
        self.root.bind("<Control-S>", self.saveas_window)
        self.root.bind("<Control-q>", self.save_and_close)
        self.root.bind("<Control-Q>", self.close)
        #self.root.protocol('WM_DELETE_WINDOW', self.save_and_close)
        
    def debug(self, message):
        self.log_box.config(state="normal")
        self.log_box.insert("end", message + "\n")
        self.log_box.config(state="disabled")
    
    def get_register_contents(self):
        regs = []
        regs.append("A : " + str(self.a))
        regs.append("B : " + str(self.b))
        regs.append("C : " + str(self.c))
        regs.append("D : " + str(self.d))
        regs.append("E : " + str(self.e))
        regs.append("F : " + str(self.f))
        regs.append("H : " + str(self.h))
        regs.append("L : " + str(self.l))
        regs.append("PC: " + str(self.PC))
        regs.append("T : " + str(self.T))
        regs.append("Z : " + str(self.z))
        regs.append("SP  : " + str(self.stackpointer))
        regs.append("MAR : " + str(self.MAR))
        regs.append("MALR: " + str(self.MALR))
        regs.append("IR  : " + str(self.IR))
        regs.append("OUT : " + str(self.OUT))
        return "\n".join(regs)

    def stop_thread(self):
        self.halt = True
        time.sleep(0.1)
        self.halt = False

    def open_file(self, filename):
        """Open a file and display the content"""
        self.filename = filename
        with open(filename, "rb") as f:
            self.raw_data = f.read()

    def save_file(self, event=None):
        """Save the current modifications into the current file"""
        with open(self.filename, "wb") as f:
            pass #f.write(unhexlify("".join(self.lines)))

    def save_and_close(self, event=None):
        self.save_file()
        self.close()

    def close(self, event=None):
        self.root.destroy()

    def saveas_window(self, event=None):
        """Open the 'save as' popup"""
        f = tk_file_dialog.asksaveasfilename(filetypes=DEFAULT_FILE_TYPES)
        if f:
            self.filename = f
            self.save_file()

    def open_window(self, event=None):
        """Open the 'open' popup"""
        f = tk_file_dialog.askopenfilename(filetypes=DEFAULT_FILE_TYPES)
        if f:
            self.open_file(f)
            
    def update(self):
        self.regs_text.set(self.get_register_contents())
        self._pulse_clock()

    def __blink__(self):
        self.led_image.config(image=self.filled_image)
        time.sleep(0.02)
        self.led_image.config(image=self.unfilled_image)

    def single_step(self):
        self.RAM = [int(i) for i in self.mem_box.get("1.0", "end-1c").replace("\n", "").split(" ")]
        if not self.halt:
            self.clock_cycle()
        
    def _pulse_clock(self):
        t = threading.Thread(target=self.__blink__)
        t.start()
    
    def run(self):
        #1 50 10 8 16 254 255 0 80 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
        self.root.mainloop()

    def exec_code(self, code=None):
        self.RAM = [int(i) for i in self.mem_box.get("1.0", "end-1c").replace("\n", "").split(" ")]
        
        self.processor_thread = threading.Thread(target=self.__exec__)
        self.processor_thread.start()
        

p = Display()
p.run()

bincodes = """01000000
00000111
01001001
00000010
01010010
00001010
01011011
00000000
00001001
00111001
10000011
10001100
00001000
11100011
11111000
00010000
00001010
11100010
10001101
00000110""".split("\n")

#for i in bincodes:
#    print(int(i, 2))

"""
64 7 73 2 82 10 91 0 9 57 131 140 8 227 248 16 10 226 141 6
"""
