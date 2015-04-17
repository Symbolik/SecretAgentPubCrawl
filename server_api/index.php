<?php
/**
 * Step 1: Require the Slim Framework
 *
 * If you are not using Composer, you need to require the
 * Slim Framework and register its PSR-0 autoloader.
 *
 * If you are using Composer, you can skip this step.
 */
require 'Slim/Slim.php';
//This is Dante's Not important line.

//This is Dante's Important line.
\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();
$app->get('/hello', function () {
$app = \Slim\Slim::getInstance();
$app->response->setStatus(200);
    echo "Hello, ";
});
$app->run();