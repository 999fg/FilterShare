<?php
require_once($_SERVER['DOCUMENT_ROOT'] . '/API/class/class_database.php');
require_once($_SERVER['DOCUMENT_ROOT'] . '/API/class/class_helper.php');

$device_id = safePost('device_id');
$data_paging = safePost('data_paging');

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
	ORDER BY filter_id DESC
	LIMIT :start, 5
",
array('start' => $data_paging));

$filter_array = $filter_query->fetchAll(PDO::FETCH_ASSOC);


foreach($filter_array as $key => $filter) {
	$final_tags = array();
	
	$tag_query = DB::getDB()->query("
		SELECT
		tag
		FROM filter_tags
		WHERE filter_id = :filter_id
	",
	array(
	'filter_id'			=> $filter['filter_id']));
	
	$tag_array = $tag_query->fetchAll(PDO::FETCH_ASSOC);
	
	foreach($tag_array as $tag) {
		$final_tags[] = $tag['tag'];
	}
	
	$filter['tags'] = $final_tags;
	
	$filter_array[$key] = $filter;
}

$data_paging = $data_paging+count($filter_array);


echo encode(array('data' => $filter_array, 'data_paging' => $data_paging));

?>