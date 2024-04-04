package com.gu.memsub.images

import io.lemonlabs.uri.Uri

case class ResponsiveImage(path: Uri, width: Int)

case class ResponsiveImageGroup(
 name: Option[String] = None,
 altText: Option[String] = None,
 metadata: Option[Grid.Metadata] = None,
 availableImages: Seq[ResponsiveImage]
)
