var express	=	require("express");
var multer	=	require('multer');
var app	=	express();
var fs = require("fs");
var storage	=	multer.diskStorage({
  destination: function (req, file, callback) {
    callback(null, './uploads');
  },
  filename: function(req, file, cb) {
    cb(null, file.originalname);
  }
});
var upload = multer({ storage : storage}).single('torrent');

app.get('/uploadTorrent',function(req,res){
	res.sendFile(__dirname + "/index.html");
});

app.get('/', function(req, res){
	console.log("ROOT");
	var directory = './uploads/';
	var files = fs.readdirSync('./uploads');
	var strHtml = "";
	var docHeader = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Custom Node.js cartridge for OpenShift</title><link href='https://fonts.googleapis.com/css?family=Ubuntu|Ubuntu+Mono' rel='stylesheet' type='text/css'><link href=\"main.css\" rel=\"stylesheet\"></head><body><header><p>Torrent List</p></header><main>";
	var htmlHeader = docHeader + "<br><br><br><h1 align=\"center\">Torrent List</h1><table align=\"center\" border cellspacing=\"0\" cellpadding=\"0\"><br><br><br><th>Torrent Id</th><th>Torrent Name</th><th>Comments</th><th>Added</th><th>Size</th>\n";
	var htmlFooter = "</main><footer><p>Developed by Claudia Rogoz 341 C3 </p></footer><script src=\"https://cdn.jsdelivr.net/riot/2.3.16/riot+compiler.min.js\"></script><script src=\"https://cdn.jsdelivr.net/superagent/0.18.0/superagent.min.js\"></script><script type=\"riot/tag\" src=\"info.tag\"></script><script>riot.mount('*')</script></body></html>";
	var endLine = "</tr>";	
		
	for (var i in files) {
		var name =  files[i];
		htmlHeader = htmlHeader + "<tr align=\"center\">";
		var stats = fs.lstatSync(directory + name);
		var time = stats.atime;		
		var size = stats.size / 10000.0;
		htmlHeader = htmlHeader + "<td>" + i + "</td>" + "<td>" + name + "</td>" + "<td>" + "" + "</td>" + "<td>" + time  + "</td>" + "<td>" + size+ "</td>" + endLine;  
	}
	htmlHeader = htmlHeader + "</table>";
	htmlHeader = htmlHeader + htmlFooter;
	res.end(htmlHeader);
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
