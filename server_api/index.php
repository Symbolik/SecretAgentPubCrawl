<?php
require 'Slim/Slim.php';
include 'db.php';
\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();
$app->get('/users','getUsers');
$app->get('/updates','getUserUpdates');
$app->get('/challenges','getChallenges');
$app->get('/dummy', 'dummyTable');
$app->get('/hello', function () {
$app = \Slim\Slim::getInstance();
$app->response->setStatus(200);


    echo "Hello, ";
});
$app->run();


function getUsers() {
$sql = "CREATE TABLE `users` (
`user_id` int(11) AUTO_INCREMENT, `username` char(10), `wins` int(100), `class` char(50),
PRIMARY KEY (`user_id`)
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
echo $sql . '{"error":{"text":'. $e->getMessage() .'}}';
}
}
function getChallenges() {
$sql = "CREATE TABLE `challenges` (
`challenge_id` int(11) AUTO_INCREMENT, `user_id` int(11),`user1` char(10), `user2` char(10), `button_chosen` char(100), `time` char(50),
PRIMARY KEY (`challenge_id`), FOREIGN KEY UFK(`user_id`) references `users`
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
echo $sql . '{"error":{"text":'. $e->getMessage() .'}}';
}
}

function getUserUpdates() {
$sql = "CREATE TABLE `updates` (
`update_id` int(11) AUTO_INCREMENT, `user_id` int(11),`location` char(10), `time` char(10),
PRIMARY KEY (`update_id`), FOREIGN KEY UFK(`user_id`) references `users`
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
echo $sql . '{"error":{"text":'. $e->getMessage() .'}}';
}
}

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
echo $sql . '{"error":{"text":'. $e->getMessage() .'}}';
}
}
