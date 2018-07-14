var smenu = document.getElementById('smenu');
var lmenu = document.getElementById('lmenu');
var gmenu = document.getElementById('gmenu');
var source = document.getElementById('source');
var sink = document.getElementById('sink');
var inte = document.getElementById('inte');
// var cal = document.getElementById('cal');
var ssetting = document.getElementById('ssetting');
var lsetting = document.getElementById('lsetting');
var gsetting = document.getElementById('gsetting');
var csetting = document.getElementById('csetting');
var sensordiv = document.getElementById('sensordiv');
var linkdiv = document.getElementById('linkdiv');
var generaldiv = document.getElementById('generaldiv');
var clusterdiv = document.getElementById('clusterdiv');
var scancel = document.getElementById('scancel');
var lcancel = document.getElementById('lcancel');
var gcancel = document.getElementById('gcancel');
var ccancel = document.getElementById('ccancel');
var sok = document.getElementById('sok');
var lok = document.getElementById('lok');
var gok = document.getElementById('gok');
var cok = document.getElementById('cok');
var sdefault = document.getElementById('sdefault');
var ldefault = document.getElementById('ldefault');
var gdefault = document.getElementById('gdefault');
var pn = document.getElementById('topdiv211');
var cpn = document.getElementById('topdiv212');
var cd = document.getElementById('topdiv311');
var pcd = document.getElementById('topdiv312');
var cl = document.getElementById("topdiv313");
var manual = document.getElementById("topdiv111");
var xmlreader = document.getElementById("topdiv112");
// var im = document.getElementById('import');
// var ex = document.getElementById('export');
// var menu = document.getElementById('menu')
var general = {
    "pmax": 6,
    "mmax": 500000,
    "snr": 9,
    "lambda_s0": 0.9,
    "lambda_c0": 0.9,
    "sr": 20,
    "tr": 0,
    "noc": 4
};

var canvas = document.querySelector('canvas');
var ctx = canvas.getContext('2d');
// ctx.font = "bold italic 20px serif";
var count = 5;
var offset = $('canvas').offset();

canvas.width = 957;
canvas.height = 558;

var RADIUS_SENSOR = [8, 13];
var nodes = [];
var links = [];
var selectedObj = null;
var currentLink = null;
var movingObj = false;
var shift = false;

function Node(x, y) {
    this.x = x;
    this.y = y;
    this.id = 0;
    this.type = 1;
    this.mouseOffsetX = 0;
    this.mouseOffsetY = 0;
    this.label = "S"+count;

    this.sqmax = 5;
    this.sbmax = 5;
    this.mu_s = 4.5;
    this.sigma_s = 0.25;
    this.mu_p = 4.5;
    this.sigma_p = 0.25;
    this.energy = 10;
    this.originalEnergy = 10
}

Node.prototype.draw = function () {
    ctx.beginPath();
    ctx.fillStyle = "black";
    ctx.arc(this.x, this.y, 3, 0, 2 * Math.PI, true);
    ctx.fill();
    var w = ctx.measureText(this.label).width;
    ctx.fillText(this.label, this.x-w/2, this.y+23);

    for (var i = 0; i <= 1; i++) {
        for (var j = 0; j <= 1; j++) {
            ctx.beginPath();
            if (this.type === 0) ctx.strokeStyle = 'red';
            else if (this.type == 1) ctx.strokeStyle = "black";
            else if (this.type == 2) ctx.strokeStyle = 'blue';
            ctx.lineWidth = 1.5;
            ctx.arc(this.x, this.y, RADIUS_SENSOR[i], Math.PI / 4 + Math.PI * j, 3 * Math.PI / 4 + Math.PI * j, false);
            ctx.stroke()
        }
    }
};

Node.prototype.containsPoint = function (x, y) {
    return (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y) < RADIUS_SENSOR[1] * RADIUS_SENSOR[1]
};

Node.prototype.mouseOffset = function (x, y) {
    this.mouseOffsetX = this.x - x;
    this.mouseOffsetY = this.y - y
};

Node.prototype.setNewPos = function (x, y) {
    this.x = this.mouseOffsetX + x;
    this.y = this.mouseOffsetY + y
};

Node.prototype.setsPointOnCircle = function (x, y) {
    var dx = x - this.x;
    var dy = y - this.y;
    var scale = Math.sqrt(dx * dx + dy * dy);
    return {
        'x': this.x + dx * RADIUS_SENSOR[1] / scale,
        'y': this.y + dy * RADIUS_SENSOR[1] / scale,
    }
};

Node.prototype.setAs = function(node){
    this.sqmax = node.sqmax;
    this.sbmax = node.sbmax;
    this.mu_s = node.mu_s;
    this.sigma_s = node.sigma_s;
    this.mu_p = node.mu_p;
    this.sigma_p = node.sigma_p;
    this.energy = node.energy
};

Node.prototype.toJson = function (x, y) {
    return {
        "x": this.x,
        "y": this.y,
        "id": this.id,
        "type": this.type,
        "q": 0,
        "sqmax": this.sqmax,
        "b": 0,
        "sbmax": this.sbmax,
        "mu_s": this.mu_s,
        "sigma_s": this.sigma_s,
        "mu_p": this.mu_p,
        "sigma_p": this.sigma_p,
        "energy": this.energy,
        "originalEnergy": this.originalEnergy
    }
};

Node.prototype.fromJson = function (j) {
    this.x = j.x;
    this.y = j.y;
    this.id = j.id;
    this.type = j.type;
    this.sqmax = j.sqmax;
    this.sbmax = j.sbmax;
    this.mu_s = j.mu_s;
    this.sigma_s = j.sigma_s;
    this.mu_p = j.mu_p;
    this.sigma_p = j.sigma_p;
    this.energy = j.energy;
    this.label = "S"+count++;
};

function Link(a, b) {
    this.id = 0;
    this.nodeA = a;
    this.nodeB = b;
    this.cbmax = 5;
    this.cb = 0;
    this.mu_t = 4.5;
    this.sigma_t = 0.25
}

Link.prototype.getEndPointsAndCircle = function () {
    var midX = (this.nodeA.x + this.nodeB.x) / 2;
    var midY = (this.nodeA.y + this.nodeB.y) / 2;
    var start = this.nodeA.setsPointOnCircle(midX, midY);
    var end = this.nodeB.setsPointOnCircle(midX, midY);
    return {
        'hasCircle': false,
        'startX': start.x,
        'startY': start.y,
        'endX': end.x,
        'endY': end.y
    }
};

Link.prototype.containsPoint = function (x, y) {
    var stuff = this.getEndPointsAndCircle();
    var dx = stuff.endX - stuff.startX;
    var dy = stuff.endY - stuff.startY;
    var length = Math.sqrt(dx * dx + dy * dy);
    var percent = (dx * (x - stuff.startX) + dy * (y - stuff.startY)) / (length * length);
    var distance = (dx * (y - stuff.startY) - dy * (x - stuff.startX)) / length;
    return (percent > 0 && percent < 1 && Math.abs(distance) < 6);
};

Link.prototype.draw = function () {
    var stuff = this.getEndPointsAndCircle();
    ctx.beginPath();
    ctx.strokeStyle = 'black';
    ctx.moveTo(stuff.startX, stuff.startY);
    ctx.lineTo(stuff.endX, stuff.endY);
    ctx.stroke();
    drawArrow(stuff.endX, stuff.endY, Math.atan2(stuff.endY - stuff.startY, stuff.endX - stuff.startX));
};

Link.prototype.setAs = function (link) {
    this.cbmax = link.cbmax;
    this.mu_t = link.mu_t;
    this.sigma_t = link.sigma_t
};

Link.prototype.toJson = function () {
    return {
        "id": this.id,
        "from": this.nodeA.id,
        "to": this.nodeB.id,
        "cbmax": this.cbmax,
        "cb": this.cb,
        "mu_t": this.mu_t,
        "sigma_t": this.sigma_t
    }
};

Link.prototype.fromJson = function (j) {
    this.id = j.id;
    this.nodeA = nodes[j.from];
    this.nodeB = nodes[j.to];
    this.cbmax = j.cbmax;
    this.cb = j.cb;
    this.mu_t = j.mu_t;
    this.sigma_t = j.sigma_t
};

function TemporaryLink(from, to) {
    this.from = from;
    this.to = to;
}

TemporaryLink.prototype.draw = function () {
    // draw the line
    ctx.beginPath();
    ctx.moveTo(this.from.x, this.from.y);
    ctx.lineTo(this.to.x, this.to.y);
    ctx.stroke();

    // draw the head of the arrow
    drawArrow(this.to.x, this.to.y, Math.atan2(this.to.y - this.from.y, this.to.x - this.from.x));
};

function removeOffsetCanvas(e) {
    return {
        'x': e.pageX - canvas.offsetLeft,
        'y': e.pageY - canvas.offsetTop
    }
}

function selecteObject(x, y) {
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].containsPoint(x, y)) return nodes[i]
    }
    for (var i = 0; i < links.length; i++) {
        if (links[i].containsPoint(x, y)) return links[i]
    }
    return null
}

function redraw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i] == selectedObj) {
            ctx.shadowColor = 'black';
            ctx.shadowBlur = 25
        }
        else {
            ctx.shadowColor = 'none';
            ctx.shadowBlur = 0
        }
        nodes[i].draw()
    }
    for (var i = 0; i < links.length; i++) {
        if (links[i] == selectedObj) {
            ctx.shadowColor = 'black';
            ctx.shadowBlur = 25
        }
        else {
            ctx.shadowColor = 'none';
            ctx.shadowBlur = 0
        }
        links[i].draw()
    }
    if (currentLink != null) {
        ctx.lineWidth = 1;
        ctx.fillStyle = ctx.strokeStyle = 'black';
        currentLink.draw();
    }
}

function drawArrow(x, y, angle) {
    var dx = Math.cos(angle);
    var dy = Math.sin(angle);
    ctx.beginPath();
    ctx.moveTo(x, y);
    ctx.lineTo(x - 8 * dx - 5 * dy, y - 8 * dy + 5 * dx);
    ctx.lineTo(x - 8 * dx + 5 * dy, y - 8 * dy - 5 * dx);
    ctx.fill();
}

function drawArc(startx, starty, endx, endy) {
    ctx.beginPath();
    ctx.moveTo(startx, starty);
    ctx.lineTo(endx, endy);
    ctx.stroke();
    var angle = Math.atan2(endy - starty, endx - startx);
    drawArrow(endx, endy, angle)
}

// Network should have source sensor
function checkSource() {
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].type == 0) return true;
    }
    return false;
}
// Network should have sink sensor
function checkSink() {
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].type == 2) return true;
    }
    return false;
}

function genXML() {
    var xml = "<WSN> \n \t";
    xml += "<Network " + "NumberOfSensors=\"" + nodes.length + "\" " + "NumberOfPackets=\"" + general.pmax + "\" " + "/> \n \t";
    xml += "<Sensors> \n \t";
    for (var i in nodes) {
        xml += "<Sensor " + "SType=\"" + nodes[i].type + "\" " + "id=\"" + nodes[i].id + "\" " + "> \n \t";
        xml += "<Position " + "X=\"" + nodes[i].x + "\" " + "Y=\"" + nodes[i].y + "\"" + "/> \n";
        xml += "<Sensor> \n";
    }
    xml += "</Sensors> \n \t";
    xml += "<Links> \n \t";
    for (var i in links) {
        xml += "<Link " + "id=\"" + links[i].id + "\"" + "> \n \t";
        xml += "<From>" + links[i].from + "</From>\n \t";
        xml += "<To>" + links[i].to + "</To>\n";
        xml += "</Links>\n";
    }
    xml += "</Links> \n";
    xml += "</WSN>";
    return xml;
}

pn.style.background = "rgba(0,102,255,1)";
pcd.style.color = "gray";

var net = {
};

function  init5sensors() {
    nodes.push(new Node(261,274));
    nodes.push(new Node(411,163));
    nodes.push(new Node(553,275));
    nodes.push(new Node(412,372));
    nodes.push(new Node(720,276));
    nodes[0].type = 0;
    nodes[4].type = 2;
    for(i = 0; i<=4; i++){
        nodes[i].label = "S"+i;
    }

    links.push(new Link(nodes[0], nodes[1]));
    links.push(new Link(nodes[0], nodes[3]));
    links.push(new Link(nodes[1], nodes[2]));
    links.push(new Link(nodes[3], nodes[2]));
    links.push(new Link(nodes[2], nodes[4]));
}
// main program - handling events
$(document).ready(function () {
    // $('document').on('contextmenu', '', function(e){ return false; });
    init5sensors();
    redraw();
    canvas.ondblclick = function (e) {
        var mouse = removeOffsetCanvas(e);
        selectedObj = selecteObject(mouse.x, mouse.y);
        if (selectedObj == null) {
            n = new Node(mouse.x, mouse.y);
            nodes.push(n);
            selectedObj = n;
            count += 1;
        }
        redraw();
    };
    canvas.onmousedown = function (e) {
        var mouse = removeOffsetCanvas(e);
        selectedObj = selecteObject(mouse.x, mouse.y);
        if (selectedObj != null) {
            if (shift) {
                currentLink = new TemporaryLink(selectedObj.setsPointOnCircle(mouse.x, mouse.y), selectedObj.setsPointOnCircle(mouse.x, mouse.y))
            }
            else if (selectedObj instanceof Node && e.button === 0) {
                movingObj = true;
                selectedObj.mouseOffset(mouse.x, mouse.y)
            }
        }
        else if (selectedObj == null) {
            smenu.style.display = 'none';
            lmenu.style.display = 'none';
            gmenu.style.display = 'none';
            currentLink = null
        }
        redraw()
    };

    canvas.onmousemove = function (e) {
        var mouse = removeOffsetCanvas(e);
        var targetNode = selecteObject(mouse.x, mouse.y);
        if (currentLink != null) {
            if (targetNode == null) {
                currentLink = new TemporaryLink(selectedObj.setsPointOnCircle(mouse.x, mouse.y), mouse)
            }
            else {
                if (targetNode instanceof Node) currentLink = new Link(selectedObj, targetNode)
            }
        }
        if (movingObj) {
            selectedObj.setNewPos(mouse.x, mouse.y)
        }
        redraw()
    };

    canvas.onmouseup = function (e) {
        if (currentLink != null) {
            if (!(currentLink instanceof TemporaryLink)) {
                selectedObj = currentLink;
                links.push(currentLink)
            }
            currentLink = null
        }
        movingObj = false;
        redraw()
    };

    canvas.oncontextmenu = function (e) {
        var mouse = removeOffsetCanvas(e);
        selectedObj = selecteObject(mouse.x, mouse.y);
        smenu.style.display = 'none';
        lmenu.style.display = 'none';
        gmenu.style.display = 'none';
        if (selectedObj != null) {
            if (selectedObj instanceof Node) {
                smenu.style.display = 'block';
                smenu.style.left = e.pageX + 'px';
                smenu.style.top = e.pageY + 'px'
            }
            else if (selectedObj instanceof Link) {
                lmenu.style.display = 'block';
                lmenu.style.left = e.pageX + 'px';
                lmenu.style.top = e.pageY + 'px'
            }
        }
        else {
            gmenu.style.display = 'block';
            gmenu.style.left = e.pageX + 'px';
            gmenu.style.top = e.pageY + 'px'
        }
        return false
    };

    document.onkeydown = function (e) {
        if (e.keyCode == 16) {
            shift = true
        }
        else if (e.keyCode == 46) {
            if (selectedObj instanceof Node) {
                var li = [];
                for (var j = 0; j < links.length; j++) {
                    if ((links[j].nodeA == selectedObj) || (links[j].nodeB == selectedObj)) {
                        li.push(j)
                    }
                }
                li.sort();
                for (var j = li.length - 1; j >= 0; j--) links.splice(li[j], 1)
                var i = nodes.indexOf(selectedObj);
                nodes.splice(i, 1);
                selectedObj = null
            }
            else if (selectedObj instanceof Link) {
                var i = links.indexOf(selectedObj);
                links.splice(i, 1);
                selectedObj = null
            }
            redraw()
        }
    };

    document.onkeyup = function (e) {
        if (e.keyCode == 16) {
            shift = false
        }
    };
    source.onclick = function (e) {
        selectedObj.type = 0;
        redraw();
        smenu.style.display = 'none'
    };
    sink.onclick = function (e) {
        selectedObj.type = 2;
        redraw();
        smenu.style.display = 'none'
    };
    inte.onclick = function (e) {
        selectedObj.type = 1;
        redraw();
        smenu.style.display = 'none'
    };
    ssetting.onclick = function (e) {
        sensordiv.style.display = 'block';
        smenu.style.display = 'none';
        if (selectedObj.type == 2) {
            $('#sqmax').attr('disabled', '')
            $('#sqmax').val(10000);
        }
        else{
            $('#sqmax').removeAttr("disabled");
            $('#sqmax').val(selectedObj.sqmax);
        }
        $('#sbmax').val(selectedObj.sbmax);
        $('#smu_s').val(selectedObj.mu_s);
        $('#ssigma_s').val(selectedObj.sigma_s);
        $('#smu_p').val(selectedObj.mu_p);
        $('#ssigma_p').val(selectedObj.sigma_p);
        $('#energy').val(selectedObj.energy);
    };
    lsetting.onclick = function (e) {
        linkdiv.style.display = 'block';
        lmenu.style.display = 'none';
        $('#cbmax').val(selectedObj.cbmax);
        $('#lmu_t').val(selectedObj.mu_t);
        $('#lsigma_t').val(selectedObj.sigma_t)
    };
    gsetting.onclick = function (e) {
        generaldiv.style.display = 'block';
        gmenu.style.display = 'none';
        $('#pmax').val(general.pmax);
        $('#mmax').val(general.mmax);
        $('#snr').val(general.snr);
        $('#ls0').val(general.lambda_s0);
        $('#lc0').val(general.lambda_c0)
    };
    csetting.onclick = function () {
        clusterdiv.style.display = 'block';
        gmenu.style.display = 'none';
        $('#sr').val(general.sr);
        $('#tr').val(general.tr);
        $('#noc').val(general.noc);

    };
    scancel.onclick = function (e) {
        sensordiv.style.display = 'none'
    };
    lcancel.onclick = function (e) {
        linkdiv.style.display = 'none'
    };
    gcancel.onclick = function (e) {
        generaldiv.style.display = 'none'
    };
    ccancel.onclick = function () {
        clusterdiv.style.display = 'none'
    };
    function process(mode) {
        if (nodes.length == 0) {
            $('#content').css('color', 'red');
            $('#content').text('No sensor');
        }
        else if (links.length == 0) {
            $('#content').css('color', 'red');
            $('#content').text('No channel');
        }
        else if (!checkSink() || !checkSource()) {
            $('#content').css('color', 'red');
            $('#content').text('No source sensor or sink sensor');
        }
        else {
            pcd.style.color = "gray";
            cd.style.color = "gray";
            cl.style.color = "gray";
            $('#content').text('');
            for (var i = 0; i < nodes.length; i++) {
                nodes[i].id = i;
                net.sensors.push(nodes[i].toJson());
            }
            for (var i = 0; i < links.length; i++) {
                links[i].id = i;
                net.channels.push(links[i].toJson())
            }
            $('#content').css('color', 'white');
            $('#content').text('Calculating . . . .');
            $.ajax(
                {
                    type: "POST",
                    url: "/cal",
                    contentType: "application/json",
                    data: JSON.stringify(net),
                    success: function (data, result) {
                        res = JSON.parse(data);
                        if(mode=="cpn") pcd.style.color = "white";
                        else if(mode=="pn") cd.style.color = "white";
                        cl.style.color = "white";
                        if (res.code == '0') {
                            // $('#content').text(result+"\r\n");
                            $('#content').text("numOfSensor: "+ nodes.length+"\r\n");
                            $('#content').append("visitedStates: "+ res.marking+"\r\n");
                            $('#content').append("Pnc: "+res.pnc+"\r\n");
                            $('#content').append("Pc: "+res.pc+"\r\n");
                        }
                        else if (res.code == '1') {
                            // $('#content').text(result+"\r\n");
                            $('#content').text(res.node);
                            // var file = new File("pnml.xml");
                            // file.open("w");
                            // file.write(res.file);
                            // file.close();
                        }
                        else if(res.code == '2'){
                            $('#content').text(res.node);
                        }
                        else if(res.code == '3'){
                            $('#content').text("No congestion");
                            nodes = [];
                            links = [];
                            var tnode = JSON.parse(res.nodes);
                            var tlink = JSON.parse(res.links);
                            for(var i in tnode){
                                var n = new Node(0,0);
                                n.fromJson(tnode[i]);
                                nodes.push(n);
                            }
                            for(var i in tlink){
                                var l = new Link(null, null);
                                l.fromJson(tlink[i]);
                                links.push(l);
                            }
                            redraw();
                        }

                        else {
                            $('#content').text("error code: " + res.code);
                        }
                    }
                }
            )
        }
    }

    cd.onclick = function () {
        if (cd.style.color != "gray") {
            net.general = general;
            net.sensors = [];
            net.channels = [];
            net.mode = "pn";
            process("pn");
        }
    };
    pcd.onclick = function () {
        if(pcd.style.color != "gray"){
            net.general = general;
            net.sensors = [];
            net.channels = [];
            net.mode="cpn";
            process("cpn");
        }
    };
    cl.onclick = function(){
        if(cl.style.color != "gray"){
            net.general = general;
            net.sensors = [];
            net.channels = [];
            net.mode="clustering";
            process("clustering");
        }
    };
    sdefault.onclick = function (e) {
        selectedObj.sqmax = Number($('#sqmax').val());
        selectedObj.sbmax = Number($('#sbmax').val());
        selectedObj.mu_s = Number($('#smu_s').val());
        selectedObj.sigma_s = Number($('#ssigma_s').val());
        selectedObj.mu_p = Number($('#smu_p').val());
        selectedObj.sigma_p = Number($('#ssigma_p').val());
        selectedObj.energy = selectedObj.originalEnergy = Number($('#energy').val());
        for(var i in nodes){
            nodes[i].setAs(selectedObj);
        }
    };
    ldefault.onclick = function (e) {
        selectedObj.cbmax = Number($('#cbmax').val());
        selectedObj.mu_t = Number($('#lmu_t').val());
        selectedObj.sigma_t = Number($('#lsigma_t').val());
        for(var i in links){
            links[i].setAs(selectedObj);
        }
    };
    gdefault.onclick = function (e) {
        $('#pmax').val(6);
        $('#mmax').val(500000);
        $('#snr').val(9);
        $('#ls0').val(0.9);
        $('#lc0').val(0.9)
    };
    sok.onclick = function (e) {
        selectedObj.sqmax = Number($('#sqmax').val());
        selectedObj.sbmax = Number($('#sbmax').val());
        selectedObj.mu_s = Number($('#smu_s').val());
        selectedObj.sigma_s = Number($('#ssigma_s').val());
        selectedObj.mu_p = Number($('#smu_p').val());
        selectedObj.sigma_p = Number($('#ssigma_p').val());
        selectedObj.energy = selectedObj.originalEnergy = Number($('#energy').val());
        sensordiv.style.display = 'none'
    };
    lok.onclick = function (e) {
        selectedObj.cbmax = Number($('#cbmax').val());
        selectedObj.mu_t = Number($('#lmu_t').val());
        selectedObj.sigma_t = Number($('#lsigma_t').val());
        linkdiv.style.display = 'none'
    };
    gok.onclick = function (e) {
        general.pmax = Number($('#pmax').val());
        general.mmax = Number($('#mmax').val());
        general.snr = Number($('#snr').val());
        general.lambda_s0 = Number($('#ls0').val());
        general.lambda_c0 = Number($('#lc0').val());
        generaldiv.style.display = 'none'
    };
    cok.onclick = function () {
      general.sr = Number($('#sr').val());
      general.tr = Number($('#tr').val());
      general.noc = Number($('#noc').val());
      clusterdiv.style.display = 'none'
    };
    pn.onclick = function (e) {
        pn.style.background = "rgba(0,102,255,1)";
        cpn.style.background = "none";
        cd.style.color = "white";
        pcd.style.color = "gray";
    };
    cpn.onclick = function (e) {
        cpn.style.background = "rgba(0,102,255,1)";
        pn.style.background = "none";
        cd.style.color = "gray";
        pcd.style.color = "white";
    };

    manual.onclick = function(){
        window.open("Manual.pdf");
    };

    xmlreader.onclick = function () {
        if(pcd.style.color != "gray"){
            window.open("cpnml.xml");
        }
        else if(cd.style.color != "gray"){
            window.open("pnml.xml");
        }
    };
    // im.onclick = function (e) {

    //    }

    //   ex.onclick = function (e) {
    // // $("#imfile").trigger("click");
    // window.location = "C:\\Users\\traml\\Desktop\\test.java"
    //   }
    // $('#xml').text("<root><child><textNode>First Child</textNode></child><child><textNode>Second Child</textNode></child><testAttrs attr1=\"attr1Value\"/></root>")
    // console.log(genXML());
    // var p = new DOMParser();
    // var doc = p.parseFromString(genXML(), "application/xml");
    // console.log(doc.documentElement);
});
