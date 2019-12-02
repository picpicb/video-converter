$(document).ready(function () {
    var movielist = "";
    getfiles();
    getrunningconversions();

    console.log(movielist);
    $("#moviefiles").on('click', '.delfile', function(){
        var idmovie = $(this).data("f");
        removefile(movielist[idmovie]);
    });

    $("#moviefiles").on('click', '.addconversion', function () {
        var idmovie = $(this).data("f");
        var myrequest = new Object();
        myrequest.path = movielist[idmovie].uri;
        myrequest.format = "none";
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("POST", "https://35.189.202.227:42308/convert", true);
        xmlhttp.setRequestHeader("Content-Type", "application/json");
        xmlhttp.send(JSON.stringify(myrequest));
        xmlhttp.onreadystatechange = function () {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                newconversion = JSON.parse(xmlhttp.responseText);
                console.log(newconversion.uuid);
                $('#conversionlist').append("<div data-conv="+newconversion.uuid+" class=\"alert alert-success\" role=\"alert\">\n" +
                    "                <h4 class=\"alert-heading\">"+movielist[idmovie].name+"</h4>\n" +
                    "                <p>Conversion en cours</p>\n" +
                    "                <hr>\n" +
                    "                <p class=\"mb-0\"><div class=\"progress\">\n" +
                    "                <div class=\"progress-bar progress-bar-striped progress-bar-animated\" role=\"progressbar\" aria-valuenow=\"0\" aria-valuemin=\"0\" aria-valuemax=\"100\" style=\"width: 0%\"></div>\n" +
                    "            </div></p>\n" +
                    "            </div>");
            }
        }
    });


    function removefile(jsonbody) {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("DELETE", "https://35.189.202.227:42308/files/{file}", true);
        xmlhttp.setRequestHeader("Content-Type", "application/json");
        xmlhttp.send(JSON.stringify(jsonbody));
        xmlhttp.onreadystatechange = function () {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200){
                getfiles();
            }
        };
    }

    function getfiles() {
        var movies = [];
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("GET", "https://35.189.202.227:42308/files", false);
        xmlhttp.onreadystatechange = function () {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200){
                movies = JSON.parse(xmlhttp.responseText);
                displaymovies(movies);
            }
        };
        xmlhttp.send(null);
    }

    function displaymovies(data) {
        $( "#moviefiles" ).empty();
        movielist = data;
        $.each(data, function(i, obj) {
            $( "#moviefiles" ).append("<li class=\"media rounded  ml-2 mr-2 mb-5 pr-4 shadow\">\n" +
                "                    <img style=\"width: 100px; height: 100px;\" src=\"../video-file.png\" class=\"mr-3\" alt=\"...\">\n" +
                "                    <div class=\"media-body\">\n" +
                "                        <div class=\"row\">\n" +
                "                            <div class=\"col-8\">\n" +
                "                                <h5 class=\"mt-0 mb-1\">"+obj.name+"</h5>\n" +
                "                                <p>Size : "+Math.round(obj.size/1000000)+"MB</p>\n" +
                "                                <p>Format : "+obj.contentType+"</p>\n" +
                "                            </div>\n" +
                "                            <div class=\"col-4 align-self-center\">\n" +
                "                                <div class=\"btn-group \" role=\"group\">\n" +
                "                                    <button data-f="+i+" type=\"button\" class=\"delfile btn btn-outline-danger\">Supprimer</button>\n" +
                "                                    <button data-f="+i+" type=\"button\" class=\"addconversion btn btn-outline-secondary\">Convertir</button>\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </li>");
        });
    }

    function getrunningconversions() {
        var interval = 1000;
        var running = [];
        $.ajax({
            type: 'GET',
            url: 'https://35.189.202.227:42308/running',
            data: $(this).serialize(),
            dataType: 'json',
            success: function (data) {
                displayrunning(data);
            },
            complete: function (data) {
                setTimeout(getrunningconversions, interval);
            }
        });
    }

    function displayrunning(data){

        $.each(data, function (i, obj) {
            uuid = obj.uuid;
            $('div[data-conv="' + uuid + '"]').find(".progress-bar").attr("aria-valuenow",obj.progression);
            $('div[data-conv="' + uuid + '"]').find(".progress-bar").width(obj.progression+"%");
            if(obj.progression == "100" && $('[data-conv="' + uuid + '"]').data("conv") == uuid ){
               $('[data-conv="' + uuid + '"]').remove();
            }
        })
    }

});

