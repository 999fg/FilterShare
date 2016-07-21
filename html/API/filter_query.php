<?php
require_once($_SERVER['DOCUMENT_ROOT'] . '/API/class/class_database.php');
require_once($_SERVER['DOCUMENT_ROOT'] . '/API/class/class_helper.php');

$device_id = safePost('device_id');
$data_paging = safePost('data_paging');

/*
$name = 'Nice Filters';
$username = 'JoshBernfeld';
$device_id = 'somedeviceid';
$tags = "#something #somethingmore";
$attributes = array('sharpen' => 10, 'contrast' => 4);
*/

/*if(!$name || !$username || !$device_id || !$attributes) {
	echo "Invalid Parameters";
	exit();
}*/

if(!$data_paging) {
	$data_paging = 0;
}

$filter_query = DB::getDB()->query("
SELECT
filter_id,
name,
username,
date_created,
atr_brightness as brightness,
atr_contrast as contrast,
atr_saturation as saturation,
atr_sharpen as sharpen,
atr_temperature as temperature,
atr_tint as tint,
atr_vignette as vignette,
atr_grain as grain
FROM filters
LIMIT :start, 5
",
array(
'start'			=> $data_paging));

$filter_array = $filter_query ->fetchAll(PDO::FETCH_ASSOC);

$data_paging = $data_paging+count($filter_array);


echo encode(array('data' => $filter_array, 'data_paging' => $data_paging));

?>