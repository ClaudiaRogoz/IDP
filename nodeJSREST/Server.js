var express	=	require("express");
var multer	=	require('multer');
var app	=	express();
var path         = require('path');
var contentTypes = require('./utils/content-types');
var fs = require("fs");
var sysInfo      = require('./utils/sys-info');
var env          = process.env;
var storage	=	multer.diskStorage({
  destination: function (req, file, callback) {
    callback(null, './uploads');
  },
  filename: function(req, file, cb) {
    cb(null, file.originalname);
  }
});
var upload = multer({ storage : storage}).single('torrent');

app.use(express.static(__dirname + '/static'));
app.get('/uploadTorrent',function(req,res){
	res.sendFile(__dirname + "/static/upload.html");
});

app.get('/', function(req, res){
	console.log("ROOT");
	var directory = './uploads/';
	var files = fs.readdirSync('./uploads');
	var strHtml = "";
	var url = '//file.html';
	var fileName = './static/file.html';
        var stream =  fs.createWriteStream(fileName);
        var files = fs.readdirSync('./uploads');
        stream.once('open', function(fd) {
        var htmlHeader = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Torrent List</title><link href='https://fonts.googleapis.com/css?family=Ubuntu|Ubuntu+Mono' rel='stylesheet' type='text/css'><link href=\"main.css\" rel=\"stylesheet\"></head><body><header><p>Torrent List</p></header><main><table style=\"width:100%\">";
        var htmlFooter = "</table></main><footer><p>Developed by Claudia Rogoz 341 C3 </p></footer><script src=\"https://cdn.jsdelivr.net/riot/2.3.16/riot+compiler.min.js\"></script><script src=\"https://cdn.jsdelivr.net/superagent/0.18.0/superagent.min.js\"></script><script type=\"riot/tag\" src=\"info.tag\"></script><script>riot.mount('*')</script></body></html>";
        var body = "";
        var html = htmlHeader;// + htmlFooter;
        var href = req.protocol + '://' + req.get('host') + req.originalUrl;;
	console.log(href);
	for (var i in files) {
                var name =  files[i];
                console.log(name);
                html = html + "<tr>";
                var stats = fs.lstatSync('./uploads/' + name);
                var time = stats.atime;
                var size = stats.size / 10000.0;
	 	var torrentHref = href + i	
                html = html + "<td>" + i + "</td>" + "<td><a href=" + torrentHref + ">" + name + "</a></td>" + "<td>" + "" + "</td>" + "<td>" + time  + "</td>" + "<td>" + size+ "</td>" + "</tr>";

        }
        html = html + htmlFooter;
        stream.end(html);
        });
	fs.readFile('./static' + url, function (err, data) {
      if (err) {
        res.writeHead(404);
        res.end();
      } else {
        let ext = path.extname(url).slice(1);
        res.setHeader('Content-Type', contentTypes[ext]);
        if (ext === 'html') {
          res.setHeader('Cache-Control', 'no-cache, no-store');
        }
        res.end(data);
      }
    });
});

app.get('/:id', function(req, res) {
	var directory = './uploads/';
	var files = fs.readdirSync('./uploads');
	var fileName = directory + files[req.params.id];
	res.download(fileName);
});


app.post('/api/photo',function(req,res){
	upload(req,res,function(err) {
		if(err) {
			return res.end("Error uploading file.");
		}
		res.end("File is uploaded");
	});
});

app.listen(3000,function(){
    console.log("Working on port 3000");
});
