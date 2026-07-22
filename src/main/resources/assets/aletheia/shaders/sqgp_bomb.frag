uniform sampler2D texture;
uniform float time;

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    vec4 color = texture2D(texture, uv);

    if (color.a > 0.01) {
        float pulse = 0.5 + 0.5 * sin(time * 6.0 + (uv.x + uv.y) * 10.0);
        vec3 sqgpColor = vec3(1.0, 0.1 + 0.4 * pulse, 0.2 + 0.8 * pulse);
        color.rgb = mix(color.rgb, sqgpColor, 0.45) + sqgpColor * pulse * 0.35;
    }

    gl_FragColor = color;
}
