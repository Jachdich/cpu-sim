general_regs = ["a", "b", "c", "d", "e", "f", "h", "l", None, None, None, None, None, None, None, None]
regs = ["a", "b", "c", "d", "e", "f", "h", "l", "pc", "sp", None, None, None, None, None, None]
regs_rev = {"ab": 0, "cd": 1, "ef": 2, "hl": 3, "a": 0, "b": 1, "c": 2, "d": 3, "e": 4, "f": 5, "h": 6, "l": 7, "pc": 8, "sp": 9}
reg2s = ["ab", "cd", "ef", "hl", None, None, None, None, None, None, None, None, None, None, None, None]
template = "\"{} {}\": 0b{}{},"

opcode_lookup = {"add": "00000000",
                 "sub": "00000001",
                 "mul": "00000010",
                 "div": "00000011",
                 "cmp": "00000100",
                 "mov": "00000101",
                 "xor": "00000110",
                 "and": "00000111",
                 "or": "00001000",
                 "xor2": "00001001",
                 "and2": "00001010",
                 "or2": "00001011",
                 "push": "00001100",
                 "pop": "00001100",
                 "inc": "00001100",
                 "jp": "00001100",
                 "adc": "00001100",
                 "sbc": "00001100",
                 "add2": "00001100",
                 "sub2": "00001100",
                 "adc2": "00001100",
                 "sbc2": "00001100",
                 "dec": "00001100",
                 "write": "00001100",
                 "read": "00001100",
                 "write_default": "00001100",
                 "read_default": "00001100",
                 "not": "00001101",
                 "not2": "00001101",
                 }

lookup_2 = {"push": "0000",
                 "pop": "0001",
                 "inc": "0010",
                 "jp": "0011",
                 "adc": "0100",
                 "sbc": "0101",
                 "add2": "0110",
                 "sub2": "0111",
                 "adc2": "1000",
                 "sbc2": "1001",
                 "dec": "1010",
                 "write": "1100",
                 "read": "1101",
                 "write_default": "1110",
                 "read_default": "1111",
                 "not": "0000",
                 "not2": "0001",}

lookup_jp = {"none": "0000",
"c": "0001",
"z": "0010",
"p": "0011",
"nc": "0100",
"nz": "0101",
"np": "0110",
"neg": "0111"}

"""
for op in ("add", "sub", "mul", "div", "cmp", "mov", "xor", "and", "or"):
    for reg in general_regs:
        for reg1 in general_regs:
            if not reg == None and not reg1 == None:
                regbin = Assembler.pad(Assembler, bin(regs_rev[reg])[2:], l=4)
                regbin += Assembler.pad(Assembler, bin(regs_rev[reg1])[2:], l=4)
                print(template.format(op, reg + "," + reg1, opcode_lookup[op], regbin))
"""

for op in ("xor2", "and2", "or2"):
    for reg in reg2s:
        for reg1 in reg2s:
            if not reg == None and not reg1 == None:
                regbin = Assembler.pad(Assembler, bin(regs_rev[reg])[2:], l=4)
                regbin += Assembler.pad(Assembler, bin(regs_rev[reg1])[2:], l=4)
                print(template.format(op, reg + "," + reg1, opcode_lookup[op], regbin))


for op in ("push", "pop", "inc", "jp", "adc", "sbc", "add2", "sub2", "adc2", "sbc2", "dec", "write", "read", "write_default", "read_default", "not", "not2"):
    if op == "jp":
        for condition in ("none", "c", "z", "p", "nc", "nz", "np", "neg"):
            thing = Assembler.pad(Assembler, lookup_jp[condition], l=4)
            if condition == "none":
                print(template.format(op, "*", opcode_lookup[op], lookup_2[op] + thing))
            else:
                print(template.format(op, condition + ",*", opcode_lookup[op], lookup_2[op] + thing))
    else:
        for reg in regs:
            if not reg == None:
                regbin = lookup_2[op] + Assembler.pad(Assembler, bin(regs_rev[reg])[2:], l=4)
                print(template.format(op, reg, opcode_lookup[op], regbin))

print(template.format("set_default_port", "", "00001101", "00100000"))
print(template.format("nop", "", "00001101", "00100001"))
print(template.format("hlt", "", "00001101", "00100010"))
