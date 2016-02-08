#!/usr/bin/python
#
import sys
version = sys.version
if version[0] == '2':
    from Tkinter import Tk, Frame, Label, Entry, StringVar
#elif version[0] == '3':
#    from tkinter import Tk, Frame, Label, Entry, StringVar


orgVar = keyVar = maxVar = resultVar = None

def init(master):
    global orgVar, keyVar, maxVar, resultVar
    
    Label(frame, text="Organisation").grid(row=0, column=0)
    Label(frame, text="Key").grid(row=1, column=0)
    Label(frame, text="Max chars").grid(row=2, column=0)
    Label(frame, text="Password").grid(row=3, column=0)
    
    orgVar = addEntry(frame, 0, 1, "")
    keyVar = addEntry(frame, 1, 1, "")
    maxVar = addEntry(frame, 2, 1, "")
    resultVar = addEntry(frame, 3, 1, "", 0)

   
def addEntry(frame, row, column, value, bindEvent=1):
    entry = Entry(frame, font="Courier")
    var = StringVar()
    entry["textvariable"] = var
    if value != None:
        var.set(value)
        
    entry.grid(row=row, column=column)
    
    if bindEvent:
        var.trace("w", calculate)
        
    return var
    
# Main code
def calculate(*args):        
    global orgVar, keyVar, maxVar, resultVar
    
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

root = Tk()
root.wm_title("Password Generator")
frame = Frame(root)
frame.pack()
init(frame)
root.mainloop()

