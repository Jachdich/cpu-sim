regs = ["a", "b", "c", "d", "e", "f", "h", "l", "pc", "sp", None, None, None, None, None, None]
general_regs = ["a", "b", "c", "d", "e", "f", "h", "l", None, None, None, None, None, None, None, None]
regs_rev = {"a": 0, "b": 1, "c": 2, "d": 3, "e": 4, "f": 5, "h": 6, "l": 7, "pc": 8, "sp": 9}
paired_regs = ["ab", "cd", "ef", "hl", None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None]
out = []
asd = []
instr = 0

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

pc on a
2 on b
add
pc off  main
mar off main

ram on a
0 on b
ir off main

#error - for two reg instructions check that regs arent the same

#add instructions
#reg_on_a, a_on_b, ALU_add, a_off_main, reset_t
for reg0 in general_regs:
    for reg1 in general_regs:
        a = 0b0
        if not reg0 == None and not reg1 == None and reg0 != reg1:
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
        if not reg0 == None and not reg1 == None:
            pair = paired_regs[regs_rev[reg0]]
            pair2 = paired_regs[regs_rev[reg]]
            if not pair == None and not pair2 == None:
                a |= getmc("{}_on_a".format(pair[0]))
                a |= getmc("{}_on_b".format(pair2[0]))
                a |= getmc("alu_xor")
                a |= getmc("{}_off_main".format(pair2[0]))
                out.append(f(a)); asd.append(len(out) - 1); instr += 1
                
                a |= getmc("{}_on_a".format(pair[1]))
                a |= getmc("{}_on_b".format(pair2[1]))
                a |= getmc("alu_xor")
                a |= getmc("{}_off_main".format(pair2[1]))
                a |= getmc("reset_t")
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
print(bin(getmc("alu_add") | getmc("reset_t") | getmc("update_flags")))
