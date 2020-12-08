function playerInit()
{
    var player = document.getElementById('player');

    player.addEventListener("ended",
            function ()
            {
                player.play();
            },
            true);
            
    $('span#pp').html('240p');
}

function changeStreamQuality(quality)
{
    var player = document.getElementById('player');
    
    player.src = 'http://127.0.0.1:8081/stream/getStream?quality=' + quality;
    player.load();
    player.play();

    $('span#pp').html(quality);
}
