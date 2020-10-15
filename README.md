# Color-Palette-Visualizer
Analyzes the color palette of one or more images and generates a visual representation.

## Purpose
The Color Palette Visualizer is meant to easily and appealingly display color breakdowns of images or series of images. Its initial purpose was to provide color breakdowns for TV shows to give insight on their color palettes. Speed, efficiency, robustness, and readability were not concerns.

## Function
This program takes as input one or more images from a file, reads them, and sorts the colors in them. It clumps together colors that are close to each other and sorts them all by prevalence in the original image. 
# Breakdowns
Color breakdowns represent this sorted group of clumps. The breakdown is a vertical bar, with rows colored by the color they represent. More rows represents more colors. The most prevalent rows are sorted at the top.
# Image Sequences
When an image sequence is given, the breakdown for each image is computed and drawn. The breakdowns are placed in order, side-by-side. The output is effectively a graph of prevalent colors vs image number/time.

## Use
Usage of this program is welcome, though, since this is a personal tool, it is not convenient. This was made to run in a console. Releases will likely not be provided.

## Contributions
Pull requests, forks, and other recommendations/changes are also welcome.
