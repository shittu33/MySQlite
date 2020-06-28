package com.example.mysqlite.Models

/**
 * Created by Abu Muhsin on 27/08/2018.
 */
class Row(var unique_row: Any, var new_value: Any) {

    fun setUnique_row(unique_row: String) {
        this.unique_row = unique_row
    }

}