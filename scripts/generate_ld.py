regs = ["a", "b", "c", "d", "e", "f", "h", "l", "pc", "sp", None, None, None, None, None, None]
general_regs = ["a", "b", "c", "d", "e", "f", "h", "l", None, None, None, None, None, None, None, None]
regs_rev = {"a": 0, "b": 1, "c": 2, "d": 3, "e": 4, "f": 5, "h": 6, "l": 7, "pc": 8, "sp": 9}

out = []
asd = []
instr = 0

MICROCODE_WORD_LEN = 53

MICROCODE_WORD = {
"msr_on_s": 0,
"alu_or": 1,
"alu_add": 2,
"pc_off_main": 3,
"f_off_main": 4,
"pc_off_s": 5,
"mar_off_main": 6,
"pc_on_a": 7,
"h_off_main": 8,
"f_on_b": 9,
"a_on_b": 10,
"l_on_a": 11,
"a_off_main": 12,
"d_off_main": 13,
"alu_sub": 14,
"z_off_main": 15,
"d_on_b": 16,
"alu_mul": 17,
"d_on_a": 18,
"l_off_main": 19,
"l_on_b": 20,
"c_off_main": 21,
"h_on_b": 22,
"malr_on_b": 23,
"h_on_a": 24,
"a_on_a": 25,
"alu_xor": 26,
"b_off_main": 27,
"c_on_b": 28,
"c_on_a": 29,
"f_on_a": 30,
"ir_off_main": 31,
"b_on_a": 32,
"alu_not": 33,
"alu_and": 34,
"malr_off_s": 35,
"z_on_s": 36,
"b_on_b": 37,
"z_on_main": 38,
"mar_off_s": 39,
"alu_div": 40,
"msr_off_main": 41,
"reset_t": 42,
"gen_2": 43,
"ram_on_a": 44,
"e_on_a": 45,
"e_on_b": 46,
"e_off_main": 47,
"sp_off_main": 48,
"sp_on_a": 49,
"update_flags": 50,
"ram_off_main": 51,
"gen_1": 52,
}
def getmc(txt):
    return bit_to_int(MICROCODE_WORD[txt])

def bit_to_int(bit):
    out = ["0"] * MICROCODE_WORD_LEN
    out[MICROCODE_WORD_LEN - 1 - bit] = "1"
    #print(out)
    return int("".join(out), base=2)

def f(a):
    return " " * 8 + "0b" + pad(bin(a)[2:]) + "L,"

def pad(bin_str, l=MICROCODE_WORD_LEN):
    return "".join(["0" for i in range(l - len(bin_str))]) + bin_str

    

##for reg in regs:
##    for reg2 in regs:
##        if reg == None or reg2 == None:
##            continue
##        if reg == reg2:
##            print(template.format(reg, "*", pad(bin(regs_rev[reg])[2:], l=4), pad(bin(regs_rev[reg])[2:], l=4)))
##        else:
##            print(template.format(reg, reg2, pad(bin(regs_rev[reg])[2:], l=4), pad(bin(regs_rev[reg2])[2:], l=4)))



#fetch decode,

a = getmc("pc_on_a")
a |= getmc("gen_2")
a |= getmc("alu_add")
a |= getmc("z_off_main")
a |= getmc("mar_off_main")
out.append(f(a))
a = getmc("z_on_s")
a |= getmc("pc_off_s")
a |= getmc("ram_on_a")
a |= getmc("alu_add")
a |= getmc("ir_off_main")
out.append(f(a))



#add instructions
#reg_on_a, a_on_b, ALU_add, a_off_main, reset_t
for reg0 in general_regs:
    for reg1 in general_regs:
        a = 0b0
        if not reg0 == None and not reg1 == None:
            a = a | getmc("{}_on_a".format(reg0))
            a = a | getmc("{}_on_b".format(reg1))
            a = a | getmc("alu_add")
            a = a | getmc("update_flags")
            a = a | getmc("{}_off_main".format(reg1))
            a = a | getmc("reset_t")
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in general_regs:
    for reg1 in general_regs:
        a = 0b0
        if not reg0 == None and not reg1 == None:
            a = a | getmc("{}_on_a".format(reg0))
            a = a | getmc("{}_on_b".format(reg1))
            a = a | getmc("alu_sub")
            a = a | getmc("update_flags")
            a = a | getmc("{}_off_main".format(reg1))
            a = a | getmc("reset_t")
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in general_regs:
    for reg1 in general_regs:
        a = 0b0
        if not reg0 == None and not reg1 == None:
            a = a | getmc("{}_on_a".format(reg0))
            a = a | getmc("{}_on_b".format(reg1))
            a = a | getmc("alu_mul")
            a = a | getmc("update_flags")
            a = a | getmc("{}_off_main".format(reg1))
            a = a | getmc("reset_t")
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in general_regs:
    for reg1 in general_regs:
        a = 0b0
        if not reg0 == None and not reg1 == None:
            a = a | getmc("{}_on_a".format(reg0))
            a = a | getmc("{}_on_b".format(reg1))
            a = a | getmc("alu_div")
            a = a | getmc("update_flags")
            a = a | getmc("{}_off_main".format(reg1))
            a = a | getmc("reset_t")
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in general_regs:
    for reg1 in general_regs:
        if reg0 == None or reg1 == None:
            a = 0b0
            out.append(f(a)); asd.append(len(out) - 1); instr += 1
            continue
        if reg0 == reg1:
            a = 0b0
            a = a | getmc("pc_on_a")
            a = a | getmc("gen_2")
            a = a | getmc("alu_add")
            a = a | getmc("pc_off_main")
            a = a | getmc("mar_off_main")
            out.append(f(a)); asd.append(len(out) - 1); instr += 1
            a = 0b0
            a = a | getmc("ram_on_a")
            a = a | getmc("alu_add")
            a = a | getmc("{}_on_b".format(reg0))
            a = a | getmc("update_flags")
            a = a | getmc("reset_t")
            out.append(f(a))
            #print(reg0, instr)
        else:
            a = 0b0
            a = a | getmc("{}_on_a".format(reg0))
            a = a | getmc("{}_on_b".format(reg1))
            a = a | getmc("alu_add")
            a = a | getmc("update_flags")
            a = a | getmc("reset_t")
            out.append(f(a)); asd.append(len(out) - 1); instr += 1

#print(pad(bin(instr)[2:],l=16))

for reg0 in regs:
    for reg1 in regs:
        if reg0 == None or reg1 == None:
            a = 0b0
            out.append(f(a)); asd.append(len(out) - 1); instr += 1
            continue
        if reg0 == reg1:
            a = 0b0
            a = a | getmc("pc_on_a")
            a = a | getmc("gen_2")
            a = a | getmc("alu_add")
            a = a | getmc("pc_off_main")
            a = a | getmc("mar_off_main")
            out.append(f(a)); asd.append(len(out) - 1); instr += 1
            a = 0b0
            a = a | getmc("ram_on_a")
            a = a | getmc("alu_add")
            a = a | getmc("{}_off_main".format(reg0))
            a = a | getmc("reset_t")
            out.append(f(a))
            #print(reg0, instr)
        else:
            a = 0b0
            a = a | getmc("{}_on_a".format(reg0))
            a = a | getmc("{}_off_main".format(reg1))
            a = a | getmc("alu_add")
            a = a | getmc("reset_t")
            out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in general_regs:
    for reg1 in general_regs:
        a = 0b0
        if not reg0 == None and not reg1 == None:
            a = a | getmc("{}_on_a".format(reg0))
            a = a | getmc("{}_on_b".format(reg1))
            a = a | getmc("alu_xor")
            a = a | getmc("update_flags")
            a = a | getmc("{}_off_main".format(reg1))
            a = a | getmc("reset_t")
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in general_regs:
    for reg1 in general_regs:
        a = 0b0
        if not reg0 == None and not reg1 == None:
            a = a | getmc("{}_on_a".format(reg0))
            a = a | getmc("{}_on_b".format(reg1))
            a = a | getmc("alu_and")
            a = a | getmc("update_flags")
            a = a | getmc("{}_off_main".format(reg1))
            a = a | getmc("reset_t")
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in general_regs:
    for reg1 in general_regs:
        a = 0b0
        if not reg0 == None and not reg1 == None:
            a = a | getmc("{}_on_a".format(reg0))
            a = a | getmc("{}_on_b".format(reg1))
            a = a | getmc("alu_or")
            a = a | getmc("update_flags")
            a = a | getmc("{}_off_main".format(reg1))
            a = a | getmc("reset_t")
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in regs:
    for reg in regs:
        a = 0b0
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in regs:
    for reg in regs:
        a = 0b0
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg0 in regs:
    for reg in regs:
        a = 0b0
        out.append(f(a)); asd.append(len(out) - 1); instr += 1

for reg in regs:
    a = 0b0
    if not reg == None:
        a |= getmc("sp_on_a")
        a |= getmc("gen_2")
        a |= getmc("alu_add")
        a |= getmc("sp_off_main")
        a |= getmc("mar_off_main")
        out.append(f(a)); asd.append(len(out) - 1); instr += 1
        a = 0b0
        a |= getmc("{}_on_a".format(reg))
        a |= getmc("alu_add")
        a |= getmc("ram_off_main")
        a |= getmc("reset_t")
        out.append(f(a))
    else:
        out.append(f(0b0)); asd.append(len(out) - 1); instr += 1

for reg in regs:
    a = 0b0
    if not reg == None:
        a |= getmc("sp_on_a")
        a |= getmc("alu_add")
        a |= getmc("mar_off_main")
        out.append(f(a)); asd.append(len(out) - 1); instr += 1
        a = 0b0
        a |= getmc("ram_on_a")
        a |= getmc("alu_add")
        a |= getmc("{}_off_main".format(reg))
        out.append(f(a))
        a = 0b0
        a |= getmc("sp_on_a")
        a |= getmc("gen_2")
        a |= getmc("alu_sub")
        a |= getmc("sp_off_main")
        a |= getmc("reset_t")
        out.append(f(a))
    else:
        out.append(f(0b0)); asd.append(len(out) - 1); instr += 1


for reg in general_regs:
    a = 0b0
    if not reg == None:
        a |= getmc("{}_on_a".format(reg))
        a |= getmc("gen_1")
        a |= getmc("alu_add")
        a |= getmc("{}_off_main".format(reg))
        a |= getmc("reset_t")
    out.append(f(a)); asd.append(len(out) - 1); instr += 1


template = """package com.cospox.customcpu;
public class Microcode {{
    public static final long[] microcode =
    {{
{}
    }};
    public static final int[] microcodeIndex =
    {{
{}
    }};
}}"""

with open("../src/main/java/com/cospox/customcpu/Microcode.java", "w") as f:
    f.write(template.format("\n".join(out), "\n        ".join([str(i) + "," for i in asd])))
#print(out)
#print(out[asd[1280] - 100: asd[1280] + 100])
