package com.ruigoncalo.social.presentation

import polanski.option.Option

class ViewResource<T> constructor(val state: ViewResourceState,
                                  val data: Option<T>,
                                  val message: Option<String>) {

    companion object {

        fun <T> success(data: T): ViewResource<T> {
            return ViewResource(ViewResourceState.SUCCESS, Option.ofObj(data), Option.none())
        }

        fun <T> error(message: String?): ViewResource<T> {
            return ViewResource(ViewResourceState.ERROR, Option.none(), Option.ofObj(message))
        }

        fun <T> loading(): ViewResource<T> {
            return ViewResource(ViewResourceState.LOADING, Option.none(), Option.none())
        }
    }
}