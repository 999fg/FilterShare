<?php
require_once('class_hidden.php');

//Display errors while on staging
if (!Hidden::$IS_PROD) {
	ini_set('display_errors',1);
	error_reporting(E_ALL);
}

class Helper
{
	public static function getUserIP()
	{
	    if (!empty($_SERVER['HTTP_CLIENT_IP']))   //check ip from share internet
	    {
	     	$ip=$_SERVER['HTTP_CLIENT_IP'];
	    }
	    elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR']))   //to check ip is pass from proxy
	    {
	      	$ip=$_SERVER['HTTP_X_FORWARDED_FOR'];
	    }
	    else
	    {
	    	$ip=$_SERVER['REMOTE_ADDR'];
	    }
	    return $ip;
	}
	

}

function encodeData($data) {
	header('Content-type: application/json');
	header("Cache-Control: no-cache"); // HTTP/1.1
	header("Expires: Sat, 26 Jul 1997 05:00:00 GMT"); // Date in the past
	echo json_encode(array("data" => $data), JSON_PRETTY_PRINT);
}

function encodeDataNoHeaders($data) {
	echo json_encode(array("data" => $data), JSON_PRETTY_PRINT);
}

function encode($data) {
	header('Content-type: application/json');
	header("Cache-Control: no-cache"); // HTTP/1.1
	header("Expires: Sat, 26 Jul 1997 05:00:00 GMT"); // Date in the past
	echo json_encode($data, JSON_PRETTY_PRINT);
}

function safeGet($key) {
	return (array_key_exists($key,$_GET) ? $_GET[$key] : NULL);
}

function safePost($key) {
	return (array_key_exists($key,$_POST) ? $_POST[$key] : NULL);
}

function safeFile($key) {
	return (array_key_exists($key,$_FILES) ? $_FILES[$key] : NULL);
}

function arrayItem($array, $key) {
	if(!isset($array)) return NULL;
	return (array_key_exists($key,$array) ? $array[$key] : NULL);
}


function beginContent() {
	ignore_user_abort(true);
	set_time_limit(60);
	ob_start();
}

function endContent() {
	echo str_repeat(" ", 4096);
	$content = ob_get_contents();
	ob_end_clean();
	$len = strlen($content);  
	header('Connection: close');         // Tell the client to close connection
	header("Content-Length: $len");     // Close connection after $size characters
	echo $content;                       // Output content
	flush();
}

function prettyPrint($a) {
    echo '<pre>'.print_r($a,1).'</pre>';
}



?>