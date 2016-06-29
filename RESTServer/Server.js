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

app.get('/', function(req, res){
	var directory = './uploads/';
	var files = fs.readdirSync('./uploads');
	var strHtml = "";
	var htmlHeader = "<br><br><br><h1 align=\"center\">Torrent List</h1><table align=\"center\" border cellspacing=\"0\" cellpadding=\"0\"><br><br><br><th>Torrent Id</th><th>Torrent Name</th><th>Comments</th><th>Added</th><th>Size</th>\n";
	var endLine = "</tr>";	
		
	for (var i in files) {
		var name =  files[i];
		htmlHeader = htmlHeader + "<tr align=\"center\">";
		var stats = fs.lstatSync(directory + name);
		var time = stats.atime;		
		var size = stats.size / 10000.0;
		console.log(stats);
		htmlHeader = htmlHeader + "<td>" + i + "</td>" + "<td>" + name + "</td>" + "<td>" + "" + "</td>" + "<td>" + time  + "</td>" + "<td>" + size+ "</td>" + endLine;  
	}
	htmlHeader = htmlHeader + "</table>";
	res.end(htmlHeader);
});

app.get('/:id', function(req, res) {
	var directory = './uploads/';
	var files = fs.readdirSync('./uploads');
	var fileName = directory + files[req.params.id];
	res.download(fileName);
});

app.get('/uploadTorrent',function(req,res){
      
	res.sendFile(__dirname + "/index.html");
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
