import java.util.ArrayList;
import library.core.*;

class Connection extends PComponent {
  private Resource start;
  private Resource end;

  private ArrayList<Segment> segments = new ArrayList<>();

  public boolean shouldRemove = false;

  class Segment {
    public PVector start;
    public PVector end;

    public Segment(PVector start, PVector end) {
      this.start = start;
      this.end = end;
    }
  }

  public Connection(Resource start, Resource end) {
    this.start = start;
    this.end = end;
  }

  public Connection(Resource start) { this.start = start; }

  public void setStartResource(Resource start) { this.start = start; }

  public void setEndResource(Resource end) { this.end = end; }

  public void addSegment(PVector start, PVector end) {
    segments.add(new Segment(start, end));
  }

  /**
   * Creates a new segment with the given start point, but null as the end
   * point. Call the finishSegment once the end point is known and the segment
   * will be automatically updated.
   */
  public void startSegment(PVector start) {
    segments.add(new Segment(start, null));
  }

  /**
   * Finishes the last segment that was started with {@link #startSegment}.
   */
  public void finishSegment(PVector end) {
    segments.get(segments.size() - 1).end = end;
  }

  /**
   * Returns a mutable reference to the list of segments.
   */
  public ArrayList<Segment> getSegments() { return segments; }

  public void draw() {
    color col = color(255);
    if (start != null && end != null) {
      if (start.amountPerMinute == end.amountPerMinute) {
        col = color(74, 217, 38); // green cause they match
      } else if (start.amountPerMinute > end.amountPerMinute) {
        col = color(227, 177, 28); // orange cause at least there is too much
                                   // input so the factory won't halt
      } else {
        col = color(217, 38,
                    38); // red cause factory will halt (too little input)
      }
    }

    stroke(col);
    strokeWeight(4);
    for (Segment segment : segments) {
      if (segment.end != null) {
        line(segment.start, segment.end);
      }
    }
  }

  public void mousePressed() {
    if (mouseButton == RIGHT) {
      shouldRemove = true;
    }
  }
}
