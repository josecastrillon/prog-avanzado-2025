<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\ServiceController;

Route::get('/services', [ServiceController::class, 'getAll']);
Route::get('/services/{id}', [ServiceController::class, 'getById']);


