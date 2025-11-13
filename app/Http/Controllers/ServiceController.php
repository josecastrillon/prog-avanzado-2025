<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class ServiceController extends Controller
{
    private $services = [
        ["id" => 1, "name" => "Mantenimiento de Computadores"],
        ["id" => 2, "name" => "Reparación de Redes"],
        ["id" => 3, "name" => "Soporte Técnico"],
    ];

    public function getAll()
    {
        return response()->json($this->services);
    }

    public function getById($id)
{
    $id = (int) $id; // <-- Convertimos el ID a número

    $service = collect($this->services)->firstWhere('id', $id);

    if (!$service) {
        return response()->json(["message" => "Service not found"], 404);
    }

    return response()->json($service);
}
}