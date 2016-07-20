<?php
require_once($_SERVER['DOCUMENT_ROOT'] . '/API/class/class_database.php');
require_once($_SERVER['DOCUMENT_ROOT'] . '/API/class/class_helper.php');

$name = 	safePost('name');
$tags = 	safePost('tags');
$username = safePost('username');
$device_id = safePost('device_id');
$attributes = safePost('filter_attributes');
$brightness = safePost('brightness');
$contrast = safePost('contrast');
$saturation = safePost('saturation');
$sharpen = safePost('sharpen');
$temperature = safePost('temperature');
$tint = safePost('tint');
$vignette = safePost('vignette');
$grain = safePost('grain');
$date_created = safePost('date_created');


/*$name = 'Nice Filters';
$username = 'JoshBernfeld';
$device_id = 'somedeviceid';
$tags = "#something #somethingmore";
#$attributes = array('sharpen' => 10, 'contrast' => 4);
$date_created = 9999;
$brightness = 0.9;
*/

if(!$name || !$username || !$device_id) {
	echo "Invalid Parameters";
	exit();
}

$tags = str_replace('#', '', $tags);
$tags_array = explode(' ', $tags);


DB::getDB()->query("
	INSERT INTO filters
	SET
	name = :name,
	username = :username,
	device_id = :device_id,
	atr_brightness = :brightness,
	atr_contrast = :contrast,
	atr_saturation = :saturation,
	atr_sharpen = :sharpen,
	atr_temperature = :temperature,
	atr_tint = :tint,
	atr_vignette = :vignette,
	atr_grain = :grain,
	date_created = :date_created,
	server_date_created = UTC_TIMESTAMP()
",
array(
'name'			=> $name,
'username' 		=> $username,
'device_id' 	=> $device_id,
'brightness' 	=> $brightness,
'contrast' 		=> $contrast,
'saturation'	=> $saturation,
'sharpen' 		=> $sharpen,
'temperature' 	=> $temperature,
'tint' 			=> $tint,
'vignette' 		=> $vignette,
'grain' 		=> $grain,
'date_created'  => $date_created));

$filter_id = DB::getDB()->connection->lastInsertId();


foreach($tags_array as $tag) {
	DB::getDB()->query("
		INSERT INTO filter_tags
		SET
		filter_id = :filter_id,
		tag = :tag
	",
	array(
	'filter_id'	=> $filter_id,
	'tag' => $tag));
}

echo encodeData(array('filter_id' => $filter_id));

?>