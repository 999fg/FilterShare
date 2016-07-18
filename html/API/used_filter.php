<?php
require_once($_SERVER['DOCUMENT_ROOT'] . '/API/class/class_database.php');
require_once($_SERVER['DOCUMENT_ROOT'] . '/API/class/class_helper.php');

$filter_id = 	safePost('filter_id');
$device_id = safePost('device_id');

/*
$name = 'Nice Filters';
$username = 'JoshBernfeld';
$device_id = 'somedeviceid';
$tags = "#something #somethingmore";
$attributes = array('sharpen' => 10, 'contrast' => 4);
*/

if(!$filter_id || !$device_id) {
	echo "Invalid Parameters";
	exit();
}


DB::getDB()->query("
	INSERT INTO filters_used
	SET
	filter_id = :filter_id,
	device_id = :device_id,
	date_created = UTC_TIMESTAMP()
",
array(
'filter_id'		=> $filter_id,
'device_id' 	=> $device_id));

$used_id = DB::getDB()->connection->lastInsertId();

echo encodeData(array('used_id' => $used_id));

?>