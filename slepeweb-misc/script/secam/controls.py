def controls(req):
    s = """<button class="controls-toggle">Controls</button>"""
    
    if "Authorization" in req.headers_in:    
        s = """
            <table><tr>
                <td><a href="/secam/app/log/secam.log">Show log</a></td>
                <td><button id="button-photo" value="photo">Take photo</button></td>
                <td><button id="button-stopgo" value="stop">Pause surveillance</button></td></td>
                <td><button id="button-refresh">Refresh</button></td>
                <td class="flash"></td>
            </tr></table>
            
            <table id="controls">
                <tr><td>Brightness</td><td><select class="ctrl" id="brightness">%s</select></td></tr>
                <tr><td>Contrast</td><td><select class="ctrl" id="contrast">%s</select></td></tr>
                <tr><td>Mode</td><td><select class="ctrl" id="mode">%s</select></td></tr>
                <tr><td>ISO</td><td><select class="ctrl" id="iso">%s</select></td></tr>
            </table>
            
            <p></p>
            <div id="dialog-trash-confirm" class="hide" title="Delete file?">
                <p>
                    <span class="ui-icon ui-icon-alert"></span>
                    Are you sure you want to delete <span id="num-files-target"></span> file(s)?
                </p>
            </div>
        """ % (int_options_stepped(10, 100, 5), int_options_stepped(-80, 100, 5), 
               str_options_arr(["auto", "night", "nightpreview", "backlight", "spotlight"]), 
               int_options_arr([0, 100, 200, 400, 800]))
    return s

def int_options_stepped(start, end, step):
    s = ""
    for i in range(start, end, step):
        s += """<option value="%d">%d</option>""" % (i, i)
    return s

def str_options_arr(lis):
    s = ""
    for ss in lis:
        s += """<option value="%s">%s</option>""" % (ss, ss)
    return s

def int_options_arr(lis):
    s = ""
    for i in lis:
        s += """<option value="%d">%d</option>""" % (i, i)
    return s