#!/usr/bin/python
#
import sys
version = sys.version
#if version[0] == '2':
from Tkinter import Tk, Frame, Label, Entry, Radiobutton, StringVar, IntVar
#elif version[0] == '3':
#from tkinter import Tk, Frame, Label, Entry, StringVar


orgVar = keyVar = maxVar = resultVar = copyVar = levelVar = None

def init(main_frame):
    global orgVar, keyVar, maxVar, resultVar, copyVar, levelVar
    
    Label(main_frame, text="Organisation").grid(row=0, column=0)
    Label(main_frame, text="Key").grid(row=1, column=0)
    Label(main_frame, text="Max chars").grid(row=2, column=0)
    Label(main_frame, text="Strength").grid(row=3, column=0)
    Label(main_frame, text="Password").grid(row=4, column=0)
    Label(main_frame, text="Copy").grid(row=5, column=0)
    
    orgVar = addTextField(main_frame, 0, 1, "halifax")
    keyVar = addTextField(main_frame, 1, 1, "g1ga50ft")
    maxVar = addTextField(main_frame, 2, 1, "")
    
    radio_subframe = Frame(main_frame)
    radio_subframe.grid(row=3, column=1, sticky="w")
    levelVar = IntVar()
    levelVar.set(0)
    addRadioButton(radio_subframe, label="Std", value=0, variable=levelVar, position=0)
    addRadioButton(radio_subframe, label="High", value=1, variable=levelVar, position=1)
    
    resultVar = addTextField(main_frame, 4, 1, "", 0)
    copyVar = addTextField(main_frame, 5, 1, "", 0)
    
   
def addTextField(frame, row, column, value, bindEvent=1):
    entry = Entry(frame, font="Courier")
    var = StringVar()
    entry["textvariable"] = var
    if value != None:
        var.set(value)
        
    entry.grid(row=row, column=column)
    
    if bindEvent:
        var.trace("w", calculate)
        
    return var
    
def addRadioButton(frame, label, value, variable, position, bindEvent=1):
    button = Radiobutton(frame, text=label, variable=variable, value=value)
    button.grid(row=0, column=position)
    
    if bindEvent:
        variable.trace("w", calculate)
        
    return button


def bumpIf(levelVar, password):
    if levelVar != None:
        mode = int(levelVar.get())
        if mode == 1:
            bumped = ""
            bump = 2
            
            for cursor in range(0, len(password)):
                # bump is in the series 1, 2, 1, 2 etc
                bump = 3 - bump
                ascii = ord(password[cursor])
                lower, upper = getRange(ascii)
                if lower > -1:
                    ascii = bumpAscii(ascii, bump, lower, upper)
                    
                bumped += chr(ascii)
            
            return bumped
    
    return password


def bumpAscii(ascii, offset, lower, upper):
    if ascii >= lower and ascii <= upper:
        ascii += offset
        if ascii > upper:
            ascii = lower + (ascii - upper - 1)
    return ascii

    
def getRange(ascii):
    if ascii >= 97 and ascii <= 122:
        return (97, 122)
    elif ascii >= 65 and ascii <= 90:
        return (65, 90)
    elif ascii >= 48 and ascii <= 57:
        return (48, 57)
    else:
        return (-1, -1)
    
# Main code
def calculate(*args):        
    global orgVar, keyVar, maxVar, resultVar, copyVar, levelVar
    
    if orgVar != None and len(orgVar.get()) >= 3 and keyVar != None and len(keyVar.get()) >= 3:        
        name = orgVar.get()
        key = keyVar.get()
        maxs = maxVar.get()
        
        #print("CALC: {0}, {1}".format(name, key))
        
        maxLength = 0 if maxs == None or len(maxs) == 0 else int(maxs)
        name = name.replace(" ", "").lower()
        strlen = len(name)
        lastchar = name[-1].upper()
        
        alen = 4;
        rem = strlen % len(key)
        index = strlen if (rem == 0) else rem
        
        partA = name[0:alen] if (strlen > alen) else name
        charmap = {"i":"1", "o":"0", "s":"5", "b":"8"}
        
        for k, v in charmap.items():
            partA = partA.replace(k, v)
        
        partA = partA + str(rem) + lastchar
        partB = key[0: index - 1] + lastchar + key[index:]
        
        res = partA + partB if (name < key) else partB + partA
        
        if maxLength > 0:
            res = res[0: maxLength]
            
        res = bumpIf(levelVar, res)
            
        step = 4
        strlen = len(res)
        answer = ""
        for cursor in range(0, strlen, step):
            if len(answer) > 0:
                answer += "."
                
            mark = cursor + step
            if mark > strlen:
                mark = strlen
                
            answer += res[cursor:mark]
            
        resultVar.set(answer)
        copyVar.set(res)

base = Tk()
base.wm_title("Password Generator")
main_frame = Frame(base)
init(main_frame)
main_frame.pack()
base.mainloop()
