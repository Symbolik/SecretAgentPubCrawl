<?php
require 'Slim/Slim.php';
include 'db.php';
\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();
$app->get('/dummy', 'dummyTable');
$app->get('/hello', function () {
$app = \Slim\Slim::getInstance();
$app->response->setStatus(200);


    echo "Hello, ";
});
$app->run();

function dummyTable() {
$sql = "CREATE TABLE `dummy` (
`dummy_key` int(11) AUTO_INCREMENT,
PRIMARY KEY (`dummy_key`)
);";

try {
$app = \Slim\Slim::getInstance();
$db = getDB();
$stmt = $db->prepare($sql);
$stmt->execute();
$db = null;
$app->response->setStatus(200);
echo 'Completed';
} catch(PDOException $e) {
echo '{"error":{"text":'. $e->getMessage() .'}}';
}
}